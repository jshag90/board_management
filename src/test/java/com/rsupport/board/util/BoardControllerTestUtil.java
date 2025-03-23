package com.rsupport.board.util;

import com.rsupport.board.vo.BoardVO;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class BoardControllerTestUtil {

    public static Stream<Arguments> failRequestSavePostVOList() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoardVO.RequestSavePost blankTitle = BoardVO.RequestSavePost
                .builder()
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        BoardVO.RequestSavePost blankContent = BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        BoardVO.RequestSavePost blankExposureStartDateTime = BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .content("this is test content")
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        BoardVO.RequestSavePost blankExposureEndDateTime = BoardVO.RequestSavePost
                .builder()
                .title("this is test title")
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .build();

        BoardVO.RequestSavePost orderExposureDateTime = BoardVO.RequestSavePost
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

    public static Stream<Arguments> failNoticeAttachmentFileWrongParameter() {

        MockMultipartFile uploadAttachmentFile = new MockMultipartFile(
                "multipartFileList", "file1.txt", "text/plain", "This is a test file".getBytes());

        MockMultipartFile tooSmallUploadAttachmentFile = new MockMultipartFile(
                "multipartFileList", "file1.txt", "text/plain", "".getBytes());


        return Stream.of(
                Arguments.arguments("post id가 음수", -1L, uploadAttachmentFile),
                Arguments.arguments("post id가 0", 0L, uploadAttachmentFile),
                Arguments.arguments("post id가 null", null, uploadAttachmentFile),
                Arguments.arguments("첨부파일에 null", 1L, null),
                Arguments.arguments("첨부파일크기가 너무 작을 경우", 1L, tooSmallUploadAttachmentFile)
        );
    }

    public static Stream<Arguments> failGetNoticeListFailWrongParameter() {

        MultiValueMap<String, String> invalidSearchTypeMap = new LinkedMultiValueMap<>();
        invalidSearchTypeMap.add("searchType", "content");
        invalidSearchTypeMap.add("searchWord", "");
        invalidSearchTypeMap.add("searchStartCreateDate", "2025-03-03");
        invalidSearchTypeMap.add("searchEndCreateDate", "2025-03-23");
        invalidSearchTypeMap.add("page", "1");
        invalidSearchTypeMap.add("pageSize", "10");

        MultiValueMap<String, String> invalidStartCreateDateFormatMap = new LinkedMultiValueMap<>();
        invalidSearchTypeMap.add("searchType", "content");
        invalidSearchTypeMap.add("searchWord", "");
        invalidSearchTypeMap.add("searchStartCreateDate", "2025-03-03 01");
        invalidSearchTypeMap.add("searchEndCreateDate", "2025-03-23");
        invalidSearchTypeMap.add("page", "1");
        invalidSearchTypeMap.add("pageSize", "10");

        MultiValueMap<String, String> invalidEndCreateDateFormatMap = new LinkedMultiValueMap<>();
        invalidEndCreateDateFormatMap.add("searchType", "content");
        invalidEndCreateDateFormatMap.add("searchWord", "");
        invalidEndCreateDateFormatMap.add("searchStartCreateDate", "2025-03-03");
        invalidEndCreateDateFormatMap.add("searchEndCreateDate", "2025-03-23 01");
        invalidEndCreateDateFormatMap.add("page", "1");
        invalidEndCreateDateFormatMap.add("pageSize", "10");

        MultiValueMap<String, String> invalidOrderWrongCreateDateFormatMap = new LinkedMultiValueMap<>();
        invalidOrderWrongCreateDateFormatMap.add("searchType", "content");
        invalidOrderWrongCreateDateFormatMap.add("searchWord", "");
        invalidOrderWrongCreateDateFormatMap.add("searchStartCreateDate", "2025-03-23");
        invalidOrderWrongCreateDateFormatMap.add("searchEndCreateDate", "2025-03-03");
        invalidOrderWrongCreateDateFormatMap.add("page", "1");
        invalidOrderWrongCreateDateFormatMap.add("pageSize", "10");

        MultiValueMap<String, String> invalidPageMap = new LinkedMultiValueMap<>();
        invalidPageMap.add("searchType", "content");
        invalidPageMap.add("searchWord", "");
        invalidPageMap.add("searchStartCreateDate", "2025-03-03");
        invalidPageMap.add("searchEndCreateDate", "2025-03-23");
        invalidPageMap.add("page", "-1");
        invalidPageMap.add("pageSize", "10");

        MultiValueMap<String, String> invalidPageSizeMap = new LinkedMultiValueMap<>();
        invalidPageSizeMap.add("searchType", "content");
        invalidPageSizeMap.add("searchWord", "");
        invalidPageSizeMap.add("searchStartCreateDate", "2025-03-03");
        invalidPageSizeMap.add("searchEndCreateDate", "2025-03-23");
        invalidPageSizeMap.add("page", "1");
        invalidPageSizeMap.add("pageSize", "-1");


        return Stream.of(
                Arguments.arguments("지원하지 않은 검색유형", invalidSearchTypeMap),
                Arguments.arguments("올바르지 않은 포맷 생성 시작일", invalidStartCreateDateFormatMap),
                Arguments.arguments("올바르지 않은 포맷 생성 종료일", invalidEndCreateDateFormatMap),
                Arguments.arguments("생성 시작일, 종료일 순서 바뀜", invalidOrderWrongCreateDateFormatMap),
                Arguments.arguments("1보다 작은 페이지 요청", invalidPageMap),
                Arguments.arguments("1보다 작은 페이지 크기 요청", invalidPageSizeMap)
        );
    }

    public static Stream<Arguments> failGetNoticePostDetailFailWrongParameter() {
        return Stream.of(
                Arguments.arguments("id값이 음수", -1L),
                Arguments.arguments("id값이 0", 0L),
                Arguments.arguments("id값이 Null", null)
        );
    }
}
