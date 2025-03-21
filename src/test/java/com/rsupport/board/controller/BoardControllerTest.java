package com.rsupport.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rsupport.board.service.BoardService;
import com.rsupport.board.utils.ErrorCode;
import com.rsupport.board.utils.SearchTypeEnum;
import com.rsupport.board.vo.BoardVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean(name="notice")
    private BoardService noticeService;

    ResultActions resultActions;

    BoardVO.RequestSavePost requestSavePost;

    BoardVO.RequestSearchPostVO requestSearchPostVO;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        requestSavePost = BoardVO.RequestSavePost
                .builder()
                .title("this is test title.")
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

        requestSearchPostVO = BoardVO.RequestSearchPostVO.builder()
                .searchType(SearchTypeEnum.title)
                .searchWord("")
                .searchStartCreateDate(LocalDate.parse("2025-03-01"))
                .searchEndCreateDate(LocalDate.parse("2025-03-19"))
                .page(1)
                .pageSize(10)
                .build();
    }

    @Nested
    @DisplayName("/board/notice: 공지사항 기본글 등록 관련")
    class saveNoticeTest {
        String url = "/board/notice";

        @Test
        @DisplayName("성공")
        void saveNoticeSuccess() throws Exception {
            //given
            String postData = objectMapper.writeValueAsString(requestSavePost);

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postData));

            //then
            verify(noticeService, times(1)).savePost(any(BoardVO.RequestSavePost.class));
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.returnCode").value(ErrorCode.SUCCESS.getReturnCode()));
        }
    }

    @Nested
    @DisplayName("/board/notice/attachment-file: 공지사항 첨부파일 등록 관련")
    class saveNoticeAttachmentFileTest {
        String url = "/board/notice/attachment-file";

        @Test
        @DisplayName("성공")
        void saveNoticeAttachmentSuccess() throws Exception {
            //given
            MockMultipartFile uploadAttachmentFile = new MockMultipartFile(
                    "multipartFileList", "file1.txt", "text/plain", "This is a test file".getBytes());

            //when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                    .file(uploadAttachmentFile)
                    .queryParam("postId","1")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

            //then
           verify(noticeService, times(1)).savePostAttachmentFiles(any(Long.class), any(List.class));
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.returnCode").value(ErrorCode.SUCCESS.getReturnCode()));
        }
    }

    @Nested
    @DisplayName("/board/notice/list: 공지사항 조회")
    class getNoticeListTest {
        String url = "/board/notice/list";

        @Test
        @DisplayName("성공")
        void saveNoticeAttachmentSuccess() throws Exception {
            //given
            MultiValueMap<String, String> requestSearchPostVOMap = new LinkedMultiValueMap<>();
            requestSearchPostVOMap.add("searchType", requestSearchPostVO.getSearchType().toString());
            requestSearchPostVOMap.add("searchWord", requestSearchPostVO.getSearchWord());
            requestSearchPostVOMap.add("searchStartCreateDate", requestSearchPostVO.getSearchStartCreateDate().toString());
            requestSearchPostVOMap.add("searchEndCreateDate", requestSearchPostVO.getSearchEndCreateDate().toString());
            requestSearchPostVOMap.add("page", String.valueOf(requestSearchPostVO.getPage()));
            requestSearchPostVOMap.add("pageSize", String.valueOf(requestSearchPostVO.getPageSize()));

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParams(requestSearchPostVOMap));

            //then
            verify(noticeService, times(1)).getPostList(any(BoardVO.RequestSearchPostVO.class));
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.returnCode").value(ErrorCode.SUCCESS.getReturnCode()));
        }
    }
}
