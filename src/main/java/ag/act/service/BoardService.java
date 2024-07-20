package ag.act.service;

import ag.act.converter.post.BoardCategoryResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Board;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.NotFoundException;
import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.model.BoardCategoryResponse;
import ag.act.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardCategoryResponseConverter boardCategoryResponseConverter;

    public Board create(Board board) {
        return boardRepository.save(board);
    }

    public List<Long> getBoardIdsByStockCodeAndCategoryIn(String stockCode, List<BoardCategory> boardCategories) {
        return boardRepository.findAllByStockCodeAndCategoryIn(stockCode, boardCategories)
            .stream()
            .map(Board::getId)
            .toList();
    }

    public Board getBoard(GetBoardGroupPostDto getBoardGroupPostDto) {
        return boardRepository.findByStockCodeAndGroupAndCategory(
                getBoardGroupPostDto.getStockCode(),
                getBoardGroupPostDto.getBoardGroup(),
                getBoardGroupPostDto.getFirstBoardCategory()
            )
            .orElseThrow(() -> new NotFoundException("게시판 그룹을 찾을수 없습니다."));
    }

    public Optional<Board> findBoard(String stockCode, BoardCategory boardCategory) {
        return boardRepository.findByStockCodeAndCategory(stockCode, boardCategory);
    }

    public Optional<Board> findBoard(Long boardId) {
        return boardRepository.findById(boardId);
    }

    public List<Board> getBoards(String stockCode) {
        return boardRepository.findAllByStockCode(stockCode);
    }

    public BoardCategoryDataArrayResponse getBoardGroupCategories(BoardGroup boardGroup) {
        final List<BoardCategoryResponse> boardCategoryResponses = boardCategoryResponseConverter.convert(boardGroup.getBoardCategoryGroups());

        return new BoardCategoryDataArrayResponse().data(boardCategoryResponses);
    }
}
