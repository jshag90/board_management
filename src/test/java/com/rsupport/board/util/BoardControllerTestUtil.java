package com.rsupport.board.util;

import com.rsupport.board.vo.BoardVO;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class BoardControllerTestUtil {

    public static Stream<Arguments> failRequestSavePostVOList() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoardVO.RequestSavePost blankTitle =  BoardVO.RequestSavePost
                .builder()
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        BoardVO.RequestSavePost blankContent =  BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        BoardVO.RequestSavePost blankExposureStartDateTime =  BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .content("this is test content")
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        BoardVO.RequestSavePost blankExposureEndDateTime =  BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .build();

        BoardVO.RequestSavePost orderExposureDateTime =  BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .build();

        return Stream.of(
                Arguments.arguments("공백 제목", blankTitle),
                Arguments.arguments("공백 내용", blankContent),
                Arguments.arguments("공백 공지시작일시", blankExposureStartDateTime),
                Arguments.arguments("공백 공지종료일시", blankExposureEndDateTime),
                Arguments.arguments("공지종료일시 시작일시 순서 바뀜", orderExposureDateTime)
        );
    }
}
