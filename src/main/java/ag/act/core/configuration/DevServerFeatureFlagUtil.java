package ag.act.core.configuration;

import ag.act.core.infra.ServerEnvironment;
import ag.act.enums.BoardCategory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DevServerFeatureFlagUtil {

    private static boolean displayableInApp = Boolean.TRUE;

    /**
     * 이 목록에 포함된 게시판 카테고리는 개발 서버에서만 앱에 노출됩니다.
     */
    private static final List<BoardCategory> featureFlaggedBoardCategories = List.of(
        BoardCategory.TEST_FOR_DEV
    );

    private final ServerEnvironment serverEnvironment;

    public static boolean isDisplayableInApp(BoardCategory boardCategory) {
        if (featureFlaggedBoardCategories.contains(boardCategory)) {
            return displayableInApp;
        }

        return Boolean.TRUE;
    }

    @PostConstruct
    public void init() {
        displayableInApp = !serverEnvironment.isProd();
    }
}
