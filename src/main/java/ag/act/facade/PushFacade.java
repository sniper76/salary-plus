package ag.act.facade;

import ag.act.converter.push.AutomatedAuthorPushResponseConverter;
import ag.act.converter.push.PushResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.admin.AutomatedAuthorPushDto;
import ag.act.dto.push.PushSearchDto;
import ag.act.entity.Push;
import ag.act.enums.push.PushSearchType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.CreatePushRequest;
import ag.act.module.push.PushSendFilter;
import ag.act.service.push.PushNotificationServiceResolver;
import ag.act.service.push.PushService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PushFacade {
    private final PushService pushService;
    private final PushSendFilter pushSendFilter;
    private final PushResponseConverter pushResponseConverter;
    private final AutomatedAuthorPushResponseConverter automatedAuthorPushResponseConverter;
    private final PushNotificationServiceResolver pushNotificationServiceResolver;

    public SimplePageDto<ag.act.model.PushDetailsResponse> getAutomatedPushes(PushSearchDto pushSearchDto, Pageable pageable) {
        validatePushSearchType(pushSearchDto);
        final Page<AutomatedAuthorPushDto> pushPage = pushService.getAutomatedPushes(pushSearchDto, pageable);
        return new SimplePageDto<>(pushPage.map(automatedAuthorPushResponseConverter::convert));
    }

    private void validatePushSearchType(PushSearchDto pushSearchDto) {
        if (pushSearchDto.getPushSearchType() == PushSearchType.STOCK_NAME
            || pushSearchDto.getPushSearchType() == PushSearchType.STOCK_GROUP_NAME) {
            throw new BadRequestException("부적합한 검색 조건입니다.");
        }
    }

    public SimplePageDto<ag.act.model.PushDetailsResponse> getPushListItems(PushSearchDto pushSearchDto, Pageable pageable) {
        final Page<Push> pushPage = pushService.getPushList(pushSearchDto, pageable);
        return new SimplePageDto<>(pushPage.map(pushResponseConverter::convert));
    }

    public ag.act.model.PushDetailsResponse createPush(CreatePushRequest createPushRequest) {
        return pushResponseConverter.convert(pushService.createPush(createPushRequest));
    }

    public ag.act.model.PushDetailsResponse getPushDetails(Long pushId) {
        return pushResponseConverter.convert(getPushNonNull(pushId));
    }

    public void deletePush(Long pushId) {
        pushService.deletePush(pushId);
    }

    public List<Push> getPushListToSend() {
        return pushSendFilter.filter(pushService.getPushListToSend());
    }

    public void sendPush(Push push) {
        pushService.updatePushToProcessing(push);
        sendPushNotification(push);
        pushService.updatePushToComplete(push);
    }

    private Push getPushNonNull(Long pushId) {
        return pushService.findPush(pushId)
            .orElseThrow(() -> new NotFoundException("해당 푸시를 찾을 수 없습니다."));
    }

    private void sendPushNotification(Push push) {
        pushNotificationServiceResolver
            .resolve(push.getPushTargetType())
            .sendPushNotification(push);
    }
}
