package com.rsupport.board.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.entity.AttachmentFile;
import com.rsupport.board.entity.BoardType;
import com.rsupport.board.entity.Notice;
import com.rsupport.board.entity.PostAttachmentFile;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.vo.BoardVO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.rsupport.board.entity.QBoardType.boardType;
import static com.rsupport.board.entity.QAttachmentFile.attachmentFile;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NoticeDao {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 공지사항 제목, 내용, 공지시작, 공지종료, 생성일, 수정일 저장
     * @param requestSavePost
     * @return
     */
    public PostDataDto.SavedPostIdDto saveNoticeInfo(BoardVO.RequestSavePost requestSavePost) {

        Notice saveNotice = Notice.builder()
                                  .title(requestSavePost.getTitle())
                                  .content(requestSavePost.getContent())
                                  .exposureStartDateTime(requestSavePost.getExposureStartDateTime())
                                  .exposureEndDateTime(requestSavePost.getExposureEndDateTime())
                                  .createDateTime(LocalDateTime.now())
                                  .modifyDateTime(LocalDateTime.now())
                                  .build();

        entityManager.persist(saveNotice);
        return PostDataDto.SavedPostIdDto.builder().postId(saveNotice.getId()).build();
    }

    /**
     * 해당 공지사항 게시글 id에 해당하는 첨부파일 저장
     * 첨부파일 테이블이 동일한 파일명, byte[]가 있으면 insert하지 않음
     * @param postId
     * @param multipartFileList
     * @throws IOException
     */
    public void saveNoticeAttachmentFiles(Long postId, List<MultipartFile> multipartFileList) throws IOException {
        List<AttachmentFile> attachmentFileList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            String originalFilename = multipartFile.getOriginalFilename();
            byte[] bytes = multipartFile.getBytes();
            AttachmentFile saveAttachmentFile = AttachmentFile.builder()
                    .fileName(originalFilename)
                    .fileData(bytes)
                    .build();

            AttachmentFile savedAttachmentFile = getSavedAttachmentFileByFileNameAndBytes(originalFilename, bytes);
            if(savedAttachmentFile != null){
                attachmentFileList.add(savedAttachmentFile);
                continue;
            }

            entityManager.persist(saveAttachmentFile);
            attachmentFileList.add(saveAttachmentFile);
        }

        BoardType noticeBoardType = jpaQueryFactory.selectFrom(boardType).where(boardType.name.eq(BoardTypeEnum.notice)).fetchOne();
        for (AttachmentFile attachmentFile : attachmentFileList) {
            PostAttachmentFile savePostAttachmentFile = PostAttachmentFile.builder().attachmentFileId(attachmentFile)
                    .postId(postId)
                    .boardTypeId(noticeBoardType)
                    .build();
            entityManager.persist(savePostAttachmentFile);
        }

        entityManager.flush();
    }

    private AttachmentFile getSavedAttachmentFileByFileNameAndBytes(String originalFilename, byte[] bytes) {
        return jpaQueryFactory.selectFrom(attachmentFile)
                .where(attachmentFile.fileName.eq(originalFilename).and(attachmentFile.fileData.eq(bytes)))
                .fetchOne();
    }

}
