package com.rsupport.board.dao;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.entity.AttachmentFile;
import com.rsupport.board.entity.BoardType;
import com.rsupport.board.entity.Notice;
import com.rsupport.board.entity.PostAttachmentFile;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.utils.SubQueryFactoryUtil;
import com.rsupport.board.vo.BoardVO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.rsupport.board.entity.QNotice.notice;
import static com.rsupport.board.entity.QBoardType.boardType;
import static com.rsupport.board.entity.QAttachmentFile.attachmentFile;
import static com.rsupport.board.entity.QPostAttachmentFile.postAttachmentFile;
import static com.rsupport.board.utils.CommonSubQueryUtil.*;
import static com.rsupport.board.utils.CommonSubQueryUtil.getCreateDateFormatSubQuery;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NoticeDao {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 공지사항 제목, 내용, 공지시작, 공지종료, 생성일, 수정일 저장
     *
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
                .writer(requestSavePost.getWriter())
                .hits(0)
                .build();

        entityManager.persist(saveNotice);
        return PostDataDto.SavedPostIdDto.builder().postId(saveNotice.getId()).build();
    }

    /**
     * 해당 공지사항 게시글 id에 해당하는 첨부파일 저장
     * 첨부파일 테이블이 동일한 파일명, byte[]가 있으면 insert하지 않음
     *
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

            AttachmentFile savedAttachmentFile = getSavedAttachmentFileByFileNameAndBytes(jpaQueryFactory, originalFilename, bytes);
            if (savedAttachmentFile != null) {
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

    }



    public List<PostDataDto.GetPostListDto> getNoticeList(BoardVO.RequestSearchPostVO requestSearchPostVO) throws ParseException {

        List<Long> coveringIndex = jpaQueryFactory.select(notice.id).from(notice)
                .where(SubQueryFactoryUtil.from(requestSearchPostVO).getWhereQuery())
                .offset(requestSearchPostVO.getOffset())
                .limit(requestSearchPostVO.getPageSize())
                .orderBy(notice.createDateTime.desc())
                .fetch();

        BooleanExpression isExistAttachmentFiles = JPAExpressions.select(postAttachmentFile.id).from(postAttachmentFile).where(postAttachmentFile.postId.eq(notice.id)).exists();
        return jpaQueryFactory.select(Projections.bean(PostDataDto.GetPostListDto.class,
                        notice.id,
                        notice.title,
                        ExpressionUtils.as(getCreateDateFormatSubQuery(notice.createDateTime), "createDateTime"),
                        ExpressionUtils.as(isExistAttachmentFiles, "isExistAttachmentFiles"),
                        notice.writer
                ))
                .from(notice)
                .where(notice.id.in(coveringIndex))
                .orderBy(notice.createDateTime.desc())
                .fetch();
    }


    public PostDataDto.GetPostDto getNoticePost(Long id) {

        List<PostDataDto.GetAttachmentFileDto> getAttachmentFileDtoList = jpaQueryFactory.select(Projections.bean(PostDataDto.GetAttachmentFileDto.class
                        , attachmentFile.id
                        , attachmentFile.fileName
                )).from(notice)
                .innerJoin(postAttachmentFile).on(notice.id.eq(postAttachmentFile.postId))
                .innerJoin(attachmentFile).on(postAttachmentFile.attachmentFileId.id.eq(attachmentFile.id))
                .innerJoin(boardType).on(postAttachmentFile.boardTypeId.id.eq(boardType.id))
                .where(boardType.name.eq(BoardTypeEnum.notice).and(notice.id.eq(id)))
                .fetch();

        PostDataDto.GetPostDto getPostDto = jpaQueryFactory.select(Projections.bean(PostDataDto.GetPostDto.class
                        , notice.id
                        , notice.title
                        , notice.content
                        , ExpressionUtils.as(getCreateDateFormatSubQuery(notice.createDateTime), "createDateTime")
                        , notice.hits
                        , notice.writer
                )).from(notice)
                .where(notice.id.eq(id))
                .fetchOne();

        if (getPostDto != null) {
            getPostDto.setAttachmentFileNameList(getAttachmentFileDtoList);
        }

        jpaQueryFactory.update(notice).set(notice.hits, notice.hits.add(1)).where(notice.id.eq(id)).execute();

        return getPostDto;
    }

    public PostDataDto.AttachmentFileDataDto downloadAttachmentFile(Long id) {
        return jpaQueryFactory.select(Projections.bean(PostDataDto.AttachmentFileDataDto.class
                        , attachmentFile.fileName
                        , attachmentFile.fileData
                )).from(attachmentFile)
                .where(attachmentFile.id.eq(id)).fetchOne();
    }

    public void updateNotice(BoardVO.RequestUpdatePostVO requestUpdatePostVO) {
        jpaQueryFactory.update(notice).set(notice.title, requestUpdatePostVO.getTitle())
                .set(notice.content, requestUpdatePostVO.getContent())
                .set(notice.modifyDateTime, LocalDateTime.now())
                .where(notice.id.eq(requestUpdatePostVO.getId())).execute();
    }

    public void deleteAttachmentFile(Long postId, List<Long> removeAttachmentFileIdList) {
        jpaQueryFactory.delete(postAttachmentFile)
                .where(postAttachmentFile.attachmentFileId.id.in(removeAttachmentFileIdList)
                        .and(postAttachmentFile.postId.eq(postId))
                        .and(postAttachmentFile.boardTypeId.name.eq(BoardTypeEnum.notice))).execute();

        for (Long attachmentFileId : removeAttachmentFileIdList) {
            if (!isExistsAttachmentFileIdMapping(jpaQueryFactory, attachmentFileId)) {
                jpaQueryFactory.delete(attachmentFile).where(attachmentFile.id.in(removeAttachmentFileIdList)).execute();
            }
        }
    }

    public void deleteNoticeById(Long postId) {
        jpaQueryFactory.delete(notice).where(notice.id.eq(postId)).execute();

        List<Long> postAttachmentFileIdList = jpaQueryFactory.select(postAttachmentFile.attachmentFileId.id)
                .from(postAttachmentFile)
                .where(postAttachmentFile.postId.eq(postId)
                        .and(postAttachmentFile.boardTypeId.name.eq(BoardTypeEnum.notice)))
                .fetch();
        deleteAttachmentFile(postId, postAttachmentFileIdList);
    }

}
