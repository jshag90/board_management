package com.rsupport.board.utils;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.rsupport.board.vo.BoardVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.time.LocalDateTime;

import static com.rsupport.board.entity.QNotice.notice;

@Setter
@Getter
@AllArgsConstructor
public class SubQueryFactoryUtil {

    private Predicate whereQuery;

    public static SubQueryFactoryUtil from(BoardVO.RequestSearchPostVO requestSearchPostVO) throws ParseException {
        return new SubQueryFactoryUtil(getNoticeWhereQuery(requestSearchPostVO));
    }

    /**
     *  공지사항 생성일
     * 검색 유형 : 제목 + 내용, 제목
     * 검색어 조건에 맞는 where쿼리 생성
     * @param requestSearchPostVO
     * @return
     */
    private static BooleanExpression getNoticeWhereQuery(BoardVO.RequestSearchPostVO requestSearchPostVO) {
        BooleanExpression searchWhere = null;
        if (!requestSearchPostVO.getSearchWord().isBlank()) {
            switch (requestSearchPostVO.getSearchType()) {
                case title -> searchWhere = notice.title.contains(requestSearchPostVO.getSearchWord());
                case title_content -> searchWhere = notice.title.contains(requestSearchPostVO.getSearchWord())
                        .or(notice.content.contains(requestSearchPostVO.getSearchWord()));
            }
        }

        BooleanExpression createDateWhereQuery;
        if (requestSearchPostVO.getSearchStartCreateDate() == null || requestSearchPostVO.getSearchEndCreateDate() == null) {
            return searchWhere;
        }

        LocalDateTime createStartLocalDateTime = requestSearchPostVO.getSearchStartCreateDate().atStartOfDay();
        LocalDateTime createEndLocalDateTime = requestSearchPostVO.getSearchEndCreateDate().atTime(23, 59, 59, 999_999_999);
        createDateWhereQuery = notice.createDateTime.between(createStartLocalDateTime, createEndLocalDateTime);
        return searchWhere == null ? createDateWhereQuery : searchWhere.and(createDateWhereQuery);
    }


}
