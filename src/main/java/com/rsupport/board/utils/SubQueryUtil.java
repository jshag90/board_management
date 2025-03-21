package com.rsupport.board.utils;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.entity.AttachmentFile;

import java.time.LocalDateTime;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.rsupport.board.entity.QAttachmentFile.attachmentFile;
import static com.rsupport.board.entity.QNotice.notice;
import static com.rsupport.board.entity.QPostAttachmentFile.postAttachmentFile;

public class SubQueryUtil {

    /**
     * 해당 첨부파일 ID를 매핑하고 있는 게시물이 있는지 여부
     *
     * @param attachmentFileId
     * @return
     */
    public static boolean isExistsAttachmentFileIdMapping(JPAQueryFactory jpaQueryFactory, Long attachmentFileId) {
        return jpaQueryFactory
                .selectOne()
                .from(postAttachmentFile)
                .where(postAttachmentFile.attachmentFileId.id.eq(attachmentFileId))
                .fetchFirst() != null;
    }

    /**
     * 파일명, 파일 byte[]로 조회
     * @param jpaQueryFactory
     * @param originalFilename
     * @param bytes
     * @return
     */
    public static AttachmentFile getSavedAttachmentFileByFileNameAndBytes(JPAQueryFactory jpaQueryFactory,String originalFilename, byte[] bytes) {
        return jpaQueryFactory.selectFrom(attachmentFile)
                .where(attachmentFile.fileName.eq(originalFilename).and(attachmentFile.fileData.eq(bytes)))
                .fetchOne();
    }

    public static StringTemplate getDateTimeFormatSubQuery(DateTimePath<LocalDateTime> createDateTime){
        return stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d %H:%i:%s')", createDateTime);
    }

    public static BooleanExpression getIsExistAttachmentFilesQuery(){
        return JPAExpressions.select(postAttachmentFile.id)
                .from(postAttachmentFile)
                .where(postAttachmentFile.postId.eq(notice.id))
                .exists();
    }


}
