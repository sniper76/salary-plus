package ag.act.service.post;

import ag.act.entity.FileContent;
import ag.act.entity.PostImage;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.repository.FileContentRepository;
import ag.act.repository.PostImageRepository;
import ag.act.util.StatusUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PostImageService {
    private final PostImageRepository postImageRepository;
    private final FileContentRepository fileContentRepository;

    public PostImageService(
        PostImageRepository postImageRepository,
        FileContentRepository fileContentRepository
    ) {
        this.postImageRepository = postImageRepository;
        this.fileContentRepository = fileContentRepository;
    }

    public List<PostImage> savePostImageList(Long postId, List<Long> imageIdList) {

        if (CollectionUtils.isEmpty(imageIdList)) {
            return Collections.emptyList();
        }

        return postImageRepository.saveAll(imageIdList.stream()
            .map(element -> {
                PostImage postImage = new PostImage();
                postImage.setPostId(postId);
                postImage.setImageId(element);
                postImage.setStatus(Status.ACTIVE);

                return postImage;
            }).toList());
    }

    public List<PostImage> findNotDeletedAllByPostId(Long postId) {
        return postImageRepository.findAllByPostIdAndStatusNotIn(postId, StatusUtil.getDeleteStatuses());
    }

    public void deletePostImageList(List<PostImage> deleteList, LocalDateTime deleteTime) {
        postImageRepository.saveAll(
            deleteList.stream().map(postImage -> setDelete(postImage, deleteTime)).toList()
        );
    }

    private PostImage setDelete(PostImage postImage, LocalDateTime now) {
        postImage.setDeletedAt(now);
        postImage.setStatus(Status.DELETED_BY_USER);
        return postImage;
    }

    public List<FileContent> getFileContentByImageIds(List<Long> imageIdList) {

        if (CollectionUtils.isEmpty(imageIdList)) {
            return Collections.emptyList();
        }

        final List<FileContent> fileContents = fileContentRepository.findByIdIn(imageIdList);
        validateContentSize(imageIdList, fileContents);

        return fileContents;
    }

    private void validateContentSize(List<Long> imageIdList, List<FileContent> fileContents) {
        if (fileContents.size() != imageIdList.size()) {
            throw new BadRequestException("첨부한 이미지를 찾을 수가 없습니다.");
        }
    }

    public List<FileContent> getFileContentsByPostId(Long postId) {
        List<PostImage> postImages = this.findNotDeletedAllByPostId(postId);
        return this.getFileContentByImageIds(postImages.stream().map(PostImage::getImageId).toList());
    }

    public void deleteAll(Long postId, LocalDateTime deleteTime) {
        deletePostImageList(findNotDeletedAllByPostId(postId), deleteTime);
    }
}
