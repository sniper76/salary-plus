package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.popup.PopupRequestConverter;
import ag.act.dto.popup.PopupSearchDto;
import ag.act.entity.Popup;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.exception.NotFoundException;
import ag.act.model.PopupRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.repository.PopupRepository;
import ag.act.service.post.PostService;
import ag.act.specification.PopupSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;
    private final PopupRequestConverter popupRequestConverter;
    private final PostService postService;

    public Page<Popup> getPopupList(PopupSearchDto popupSearchDto, Pageable pageable) {
        if (popupSearchDto.isKeywordBlank()) {
            return popupRepository.findAll(
                popupSearchDto
                    .getTargetDatetimeSpecification()
                    .and(PopupSpecification.isActive()),
                pageable
            );
        }

        return popupRepository.findAll(
            popupSearchDto
                .getTitleContainsSpecification()
                .and(popupSearchDto.getTargetDatetimeSpecification())
                .and(PopupSpecification.isActive()),
            pageable
        );
    }

    public Optional<Popup> getPopup(Long popupId) {
        return popupRepository.findByIdAndStatus(popupId, Status.ACTIVE);
    }

    public Popup save(Popup popup) {
        return popupRepository.save(popup);
    }

    public Popup createPopup(PopupRequest popupRequest) {
        return save(popupRequestConverter.convert(popupRequest, getPost(popupRequest.getPostId())));
    }

    public Popup updatePopup(PopupRequest popupRequest, Popup popup) {
        return save(popupRequestConverter.convert(popupRequest, popup, getPost(popupRequest.getPostId())));
    }

    private Post getPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return postService.getPostNotDeleted(postId);
    }

    public Popup getExclusivePopup(PopupDisplayTargetType displayTargetType, String stockCode) {
        final User user = ActUserProvider.getNoneNull();

        return getExclusivePopup(
            displayTargetType, Stream.of(stockCode).filter(Objects::nonNull).toList(), user
        );
    }

    private Popup getExclusivePopup(PopupDisplayTargetType displayTargetType, List<String> stockCode, User user) {
        return Stream.<Supplier<Optional<Popup>>>of(
                () -> popupRepository.findExclusiveByStockTargetTypeIsStockGroup(displayTargetType.name(), stockCode, user.getId()),
                () -> popupRepository.findExclusiveByStockTargetTypeIsStock(displayTargetType.name(), stockCode, user.getId()),
                () -> popupRepository.findExclusiveByStockTargetTypeIsAll(displayTargetType.name())
            ).parallel().map(Supplier::get).filter(Optional::isPresent).map(Optional::get)
            .min(Comparator.comparing(it -> it.getLinkType().getSortOrder()))
            .orElse(null);
    }

    public SimpleStringResponse deletePopup(Long popupId) {
        Popup popup = getPopup(popupId).orElseThrow(
            () -> new NotFoundException("해당 팝업이 존재하지 않습니다.")
        );

        if (LocalDateTime.now().isBefore(popup.getTargetStartDatetime())) {
            // 현재 예약 상태인 팝업은 완전삭제
            popupRepository.deleteById(popupId);
        } else {
            popup.setStatus(Status.DELETED_BY_ADMIN);
        }

        return new ag.act.model.SimpleStringResponse().status("ok");
    }
}
