package com.rsupport.board.dao;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.entity.AttachmentFile;
import com.rsupport.board.entity.BoardType;
import com.rsupport.board.entity.Notice;
import com.rsupport.board.entity.PostAttachmentFile;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.utils.SubQueryUtil;
import com.rsupport.board.utils.WhereSubQueryFactoryUtil;
import com.rsupport.board.vo.BoardVO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import static com.rsupport.board.utils.SubQueryUtil.*;
import static com.rsupport.board.utils.SubQueryUtil.getDateTimeFormatSubQuery;

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
    @CacheEvict(value = "noticeListCache", allEntries = true)
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
    @Caching(evict = {
            @CacheEvict(value = "noticeListCache", allEntries = true),
            @CacheEvict(value = "noticePostDataCache", key = "#postId")
    })
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

            if(SubQueryUtil.isAlreadyPostAttachmentFile(jpaQueryFactory, postId, noticeBoardType, attachmentFile)) {
                continue;
            }

            entityManager.persist(savePostAttachmentFile);
        }

    }

    /**
     * 공지사항 목록 검색
     * @param requestSearchPostVO
     * @return
     * @throws ParseException
     */

    @Cacheable(value = "noticeListCache", key = "#requestSearchPostVO")
    public List<PostDataDto.GetPostListDto> getNoticeList(BoardVO.RequestSearchPostVO requestSearchPostVO) throws ParseException {

        List<Long> coveringIndex = jpaQueryFactory.select(notice.id).from(notice)
                .where(WhereSubQueryFactoryUtil.from(requestSearchPostVO).getWhereQuery())
                .offset(requestSearchPostVO.getOffset())
                .limit(requestSearchPostVO.getPageSize())
                .orderBy(notice.exposureStartDateTime.asc())
                .fetch();

        return jpaQueryFactory.select(Projections.bean(PostDataDto.GetPostListDto.class,
                        notice.id,
                        notice.title,
                        ExpressionUtils.as(getDateTimeFormatSubQuery(notice.createDateTime), "createDateTime"),
                        ExpressionUtils.as(getDateTimeFormatSubQuery(notice.exposureStartDateTime), "exposureStartDateTime"),
                        ExpressionUtils.as(getDateTimeFormatSubQuery(notice.exposureEndDateTime), "exposureEndDateTime"),
                        ExpressionUtils.as(getIsExistAttachmentFilesQuery(), "isExistAttachmentFiles"),
                        notice.writer
                ))
                .from(notice)
                .where(notice.id.in(coveringIndex))
                .orderBy(notice.exposureStartDateTime.asc())
                .fetch();
    }

    /**
     * 특정 공지사항 글 조회
     * @param id
     * @return
     */
    @Cacheable(value = "noticePostDataCache", key = "#id")
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
                        , ExpressionUtils.as(getDateTimeFormatSubQuery(notice.createDateTime), "createDateTime")
                        , notice.hits
                        , notice.writer
                )).from(notice)
                .where(notice.id.eq(id))
                .fetchOne();

        if (getPostDto != null) {
            getPostDto.setAttachmentFileNameList(getAttachmentFileDtoList);
        }
        return getPostDto;
    }

    /**
     * 조회수 갱신
     * @param postId
     */
    @CacheEvict(value = "noticePostDataCache", key = "#postId")
    public void updateNoticeHits(Long postId){
        jpaQueryFactory.update(notice).set(notice.hits, notice.hits.add(1)).where(notice.id.eq(postId)).execute();
    }

    /**
     * 첨부파일 조회
     * @param id
     * @return
     */
    public PostDataDto.AttachmentFileDataDto downloadAttachmentFile(Long id) {
        return jpaQueryFactory.select(Projections.bean(PostDataDto.AttachmentFileDataDto.class
                        , attachmentFile.fileName
                        , attachmentFile.fileData
                )).from(attachmentFile)
                .where(attachmentFile.id.eq(id)).fetchOne();
    }

    /**
     * 특정 공지사항 수정
     * @param requestUpdatePostVO
     */
    @CacheEvict(value = "noticePostDataCache", key = "#requestUpdatePostVO.id")
    public void updateNotice(BoardVO.RequestUpdatePostVO requestUpdatePostVO) {
        jpaQueryFactory.update(notice).set(notice.title, requestUpdatePostVO.getTitle())
                .set(notice.content, requestUpdatePostVO.getContent())
                .set(notice.modifyDateTime, LocalDateTime.now())
                .where(notice.id.eq(requestUpdatePostVO.getId())).execute();
    }

    /**
     * 특정 공지사항 글 첨부파일 삭제
     * @param postId
     * @param removeAttachmentFileIdList
     */
    @Caching(evict = {
            @CacheEvict(value = "noticeListCache", allEntries = true),
            @CacheEvict(value = "noticePostDataCache", key = "#postId")
    })
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

    /**
     * 특정 공지사항 글 삭제
     * @param postId
     */
    @Caching(evict = {
            @CacheEvict(value = "noticeListCache", allEntries = true),
            @CacheEvict(value = "noticePostDataCache", key = "#postId")
    })
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
