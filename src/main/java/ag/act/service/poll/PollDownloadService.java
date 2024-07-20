package ag.act.service.poll;

import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.BadRequestException;
import ag.act.service.download.csv.CsvDownloadSourceProvider;
import ag.act.service.download.csv.PollCsvDownloadService;
import ag.act.util.FilenameUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PollDownloadService {
    private final PollCsvDownloadService pollCsvDownloadService;

    public String getCsvFilename(Post post) {
        final Stock stock = post.getBoard().getStock();
        return FilenameUtil.getFilename(stock.getName(), post.getTitle(), "csv");
    }

    public void downloadPostPollResultsInCsv(Post post, HttpServletResponse response) {
        validateBoardGroupAndCategory(post);
        pollCsvDownloadService.download(response, new CsvDownloadSourceProvider<>(List.of(post)));
    }

    private void validateBoardGroupAndCategory(Post post) {
        Board board = post.getBoard();
        BoardGroup boardGroup = board.getGroup();
        BoardCategory boardCategory = board.getCategory();

        if (!isValidSurveyBoard(boardGroup, boardCategory)) {
            throw new BadRequestException("해당 글은 설문이 아닙니다.");
        }
    }

    private boolean isValidSurveyBoard(BoardGroup boardGroup, BoardCategory boardCategory) {
        return boardGroup == BoardGroup.ACTION && boardCategory == BoardCategory.SURVEYS;
    }
}
