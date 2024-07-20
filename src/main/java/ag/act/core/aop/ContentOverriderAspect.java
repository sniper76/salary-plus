package ag.act.core.aop;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.annotation.ContentOverrider;
import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.Comment;
import ag.act.entity.Content;
import ag.act.entity.Post;
import ag.act.entity.Report;
import ag.act.entity.User;
import ag.act.exception.InternalServerException;
import ag.act.model.Status;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.service.ReportService;
import ag.act.service.user.UserHoldingStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ContentOverriderAspect {

    private static final String USER_HOLDING_STOCKS = "USER_HOLDING_STOCKS";
    private static final String REPORTED_POSTS_BY_ME = "REPORTED_POSTS_BY_ME";
    private static final String REPORTED_COMMENTS_BY_ME = "REPORTED_COMMENTS_BY_ME";
    private static final String TITLE_FIELD = "title";
    private static final String CONTENT_FIELD = "content";
    private final ReportService reportService;
    private final UserHoldingStockService userHoldingStockService;

    @Around("@annotation(ag.act.core.annotation.ContentOverrider)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        return overrideContents(joinPoint, findMethodAnnotation(joinPoint));
    }

    private Object overrideContents(ProceedingJoinPoint joinPoint, ContentOverrider contentOverrider) throws Throwable {

        final Object result = joinPoint.proceed();

        try {
            final Content contentParameter = findFirstParameter(joinPoint);
            validateContent(contentParameter);

            final Set<String> fieldNames = getOverridableFields(contentOverrider.value());

            initReportedByDefault(result);
            initDeletedByDefault(result);

            if (Status.DELETED_BY_USER == contentParameter.getStatus()) {
                setDeleted(result, fieldNames, contentParameter.getDeletedMessage());
            } else if (Status.DELETED_BY_ADMIN == contentParameter.getStatus()) {
                setDeleted(result, fieldNames, contentParameter.getDeletedByAdminMessage());
            }

            if (isReportedContent(contentParameter)) {
                setReported(result, fieldNames, contentParameter.getReportedMessage());
            } else if (isExclusiveToHolders(contentParameter)) {
                setExclusiveToHolders(contentParameter, result, fieldNames, contentParameter.getExclusiveToHoldersMessage());
            }

            return result;
        } catch (Throwable ex) {
            log.error("Error occurred while applying @ContentOverrider", ex);
            throw new InternalServerException("데이터를 가공하는 중에 알 수 없는 오류가 발생하였습니다.", ex);
        }
    }

    private Set<String> getOverridableFields(String value) {
        final Set<String> mergedSet = new HashSet<>(Set.of(TITLE_FIELD, CONTENT_FIELD));
        mergedSet.add(value);

        return mergedSet;
    }

    private void validateContent(Content contentParameter) {
        final Class<? extends Content> parameterClass = contentParameter.getClass();
        if (parameterClass == Post.class
            || parameterClass == Comment.class
            || parameterClass == User.class
        ) {
            return;
        }

        log.error("@ContentOverrider can be applied only for Post, Comment or User in the first parameter of the method");
        throw new IllegalArgumentException("@ContentOverrider can be applied only for Post, Comment or User");
    }

    private boolean isExclusiveToHolders(Content contentParameter) {
        // TODO 나중에 Post 뿐만 아니라 다른 Content 들도 getIsExclusiveToHolders() 가 생긴다면
        //      그때는 이 instanceof 를 변경해야 한다.
        if (contentParameter instanceof Post postContent) {
            return postContent.getIsExclusiveToHolders();
        }
        return false;
    }

    private boolean isReportedContent(Content contentParameter) {
        return (contentParameter.getClass() == Post.class && getReportedPostMapByMe().containsKey(contentParameter.getId()))
            || (contentParameter.getClass() == Comment.class && getReportedCommentMapByMe().containsKey(contentParameter.getId()));
    }

    private void initReportedByDefault(Object result) {
        setFieldValue(result, "reported", false);
    }

    private void initDeletedByDefault(Object result) {
        setFieldValue(result, "deleted", false);
    }

    private void setReported(Object result, Set<String> fieldNames, String replaceMessage) {
        setValues(result, fieldNames, replaceMessage, "reported");
    }

    private void setExclusiveToHolders(Content contentParameter, Object result, Set<String> fieldNames, String replaceMessage) {
        if (contentParameter instanceof Post postContent) {
            if (!userHasStockOf(postContent)) {
                setValues(result, fieldNames, replaceMessage);
            }
        }
    }

    private void setDeleted(Object result, Set<String> fieldNames, String replaceMessage) {
        setValues(result, fieldNames, replaceMessage, "deleted");
        setFieldValue(result, "userProfile", null);
    }

    private void setValues(Object result, Set<String> fieldNames, String replaceMessage, String booleanFieldName) {
        setValues(result, fieldNames, replaceMessage);
        setFieldValue(result, booleanFieldName, true);
    }

    private void setValues(Object result, Set<String> fieldNames, String replaceMessage) {
        if (isCmsApi()) {
            return;
        }
        fieldNames.forEach(fieldName -> setFieldValue(result, fieldName, replaceMessage));
    }

    private void setFieldValue(Object result, String fieldName, Object value) {
        try {
            final Field messageField = result.getClass().getDeclaredField(fieldName);
            messageField.setAccessible(true);
            messageField.set(result, value);
        } catch (NoSuchFieldException e) {
            log.debug("No such field: {}", fieldName);
        } catch (IllegalAccessException e) {
            log.debug("Illegal access: {}", fieldName);
        }
    }

    private boolean isCmsApi() {
        return RequestContextHolder.isCmsApi();
    }

    private boolean userHasStockOf(Post postContent) {
        return getUserHoldingStocksMap().containsKey(postContent.getBoard().getStockCode());
    }

    private Map<Long, Report> getReportedPostMapByMe() {
        return getCachedAttribute(REPORTED_POSTS_BY_ME, () -> {
            Map<Long, Report> reportedPostsByMe = ActUserProvider.get()
                .map(user -> reportService.getReportedPostMapByMe())
                .orElseGet(Map::of);
            RequestContextHolder.getRequest().setAttribute(REPORTED_POSTS_BY_ME, reportedPostsByMe);

            return reportedPostsByMe;
        });
    }

    private Map<Long, Report> getReportedCommentMapByMe() {
        return getCachedAttribute(REPORTED_COMMENTS_BY_ME, () -> {
            final Map<Long, Report> reportedCommentsByMe = ActUserProvider.get()
                .map(user -> reportService.getReportedCommentMapByMe())
                .orElseGet(Map::of);
            RequestContextHolder.getRequest().setAttribute(REPORTED_COMMENTS_BY_ME, reportedCommentsByMe);
            return reportedCommentsByMe;
        });
    }

    private Map<String, SimpleStock> getUserHoldingStocksMap() {
        return getCachedAttribute(USER_HOLDING_STOCKS, () -> {
            final Map<String, SimpleStock> userHoldingStocksMap = ActUserProvider.get()
                .map(user -> {
                    final Long currentUserId = ActUserProvider.getNoneNull().getId();
                    return userHoldingStockService.findAllSimpleStocksByUserId(currentUserId)
                        .stream()
                        .collect(Collectors.toMap(SimpleStock::getCode, Function.identity()));
                })
                .orElseGet(Map::of);

            RequestContextHolder.getRequest().setAttribute(USER_HOLDING_STOCKS, userHoldingStocksMap);
            return userHoldingStocksMap;
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T getCachedAttribute(String key, Supplier<T> mapSupplier) {
        return Optional.ofNullable((T) RequestContextHolder.getRequest().getAttribute(key))
            .orElseGet(mapSupplier);
    }

    private Content findFirstParameter(ProceedingJoinPoint joinPoint) {
        return (Content) joinPoint.getArgs()[0];
    }

    private ContentOverrider findMethodAnnotation(ProceedingJoinPoint joinPoint) {

        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final ContentOverrider contentOverrider = AnnotationUtils.findAnnotation(method, ContentOverrider.class);
        Objects.requireNonNull(contentOverrider, "Can not find ReportedContent annotation on method");

        return contentOverrider;
    }
}
