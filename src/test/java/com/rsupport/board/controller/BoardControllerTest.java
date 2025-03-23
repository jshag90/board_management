package com.rsupport.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rsupport.board.service.BoardService;
import com.rsupport.board.utils.ReturnCode;
import com.rsupport.board.utils.SearchTypeEnum;
import com.rsupport.board.vo.BoardVO;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));
        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failRequestSavePostVOList")
        void saveNoticeFailWrongParameter(String testTitle, BoardVO.RequestSavePost invalidRequestSavePostVO) throws Exception{

            //given
            String postData = objectMapper.writeValueAsString(invalidRequestSavePostVO);

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postData));

            //then
            verify(noticeService, times(0)).savePost(any(BoardVO.RequestSavePost.class));
            resultActions.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));

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
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));
        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failNoticeAttachmentFileWrongParameter")
        void saveNoticeAttachmentFailWrongParameter(String testTitle, Long wrongPostId, MockMultipartFile invalidUploadAttachmentFile) throws Exception {

            ResultActions resultActions;
            if (invalidUploadAttachmentFile != null) {
                resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                        .file(invalidUploadAttachmentFile)
                        .queryParam("postId", String.valueOf(wrongPostId))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
            } else {
                //given when
                resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                        .queryParam("postId", String.valueOf(wrongPostId))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
            }

            //then
            verify(noticeService, times(0)).savePostAttachmentFiles(any(Long.class), any(List.class));
            resultActions.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));
        }
    }

    @Nested
    @DisplayName("/board/notice/list: 공지사항 목록 조회")
    class getNoticeListTest {
        String url = "/board/notice/list";

        @Test
        @DisplayName("성공")
        void getNoticeListSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));
        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failGetNoticeListFailWrongParameter")
        void getNoticeListFailWrongParameter(String testTitle,  MultiValueMap<String, String> requestSearchPostVOMap) throws Exception{

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParams(requestSearchPostVOMap));

            //then
            verify(noticeService, times(0)).getPostList(any(BoardVO.RequestSearchPostVO.class));
            resultActions.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));

        }
    }

    @Nested
    @DisplayName("/board/notice/detail: 게시판 게시글 조회")
    class getNoticePostDetailTest {
        String url = "/board/notice/detail";

        @Test
        @DisplayName("성공")
        void getNoticePostDetailTestSuccess() throws Exception {
            //given
            Long id = 1L;

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("id", String.valueOf(id)));

            //then
            verify(noticeService, times(1)).getPostData(any(Long.class));
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));
        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failSingleIdFailWrongParameter")
        void getNoticeListFailWrongParameter(String testTitle,  Long id) throws Exception{

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("id", String.valueOf(id)));

            //then
            verify(noticeService, times(0)).getPostData(any(Long.class));
            resultActions
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));

        }
    }

    @Nested
    @DisplayName("/board/notice/attachment-file: 게시판 게시글 첨부파일 다운로드")
    class downloadNoticeAttachmentFileTest {
        String url = "/board/notice/attachment-file";

        @Test
        @DisplayName("성공")
        void downloadNoticeAttachmentFileSuccess() throws Exception {
            //given
            Long id = 1L;

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .param("id", String.valueOf(id))
                    .accept(MediaType.APPLICATION_JSON_VALUE));

            //then
            verify(noticeService, times(1)).downloadAttachmentFile(any(HttpServletResponse.class), any(Long.class));
        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failSingleIdFailWrongParameter")
        void downloadNoticeAttachmentFileFailWrongParameter(String testTitle,  Long id) throws Exception{

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .param("id", String.valueOf(id))
                    .accept(MediaType.APPLICATION_JSON_VALUE));

            //then
            verify(noticeService, times(0)).downloadAttachmentFile(any(HttpServletResponse.class), any(Long.class));
            resultActions
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));


        }
    }

    @Nested
    @DisplayName("/board/notice: 게시판 게시글의 제목, 내용 수정")
    class updatePostTest {
        String url = "/board/notice";

        BoardVO.RequestUpdatePostVO requestUpdatePostVO;
        @BeforeEach
        void setUp(){
            requestUpdatePostVO = BoardVO.RequestUpdatePostVO.builder()
                    .id(1L)
                    .title("This is Update title.")
                    .content("This is Update content.")
                    .build();
        }

        @Test
        @DisplayName("성공")
        void updatePostSuccess() throws Exception {
            //given
            String postData = objectMapper.writeValueAsString(requestUpdatePostVO);

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(postData));

            //then
            verify(noticeService, times(1)).updatePost(any(BoardVO.RequestUpdatePostVO.class));
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));

        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failUpdatePostFailWrongParameter")
        void updatePostFailWrongParameter(String testTitle, BoardVO.RequestUpdatePostVO invalidUpdatePostVO) throws Exception{

            //given
            String postData = objectMapper.writeValueAsString(invalidUpdatePostVO);

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(postData));

            //then
            verify(noticeService, times(0)).updatePost(any(BoardVO.RequestUpdatePostVO.class));
            resultActions.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));

        }
    }

    @Nested
    @DisplayName("/board/notice/attachment-file:게시판 첨부파일 목록 수정")
    class updatePostAttachmentFileTest {
        String url = "/board/notice/attachment-file";

        @Test
        @DisplayName("성공: 게시판 첨부파일 목록 수정")
        void updatePostAttachmentFileSuccess() throws Exception {

            //given
            Long postId = 1L;
            List<Long> removeAttachmentFileIdList = List.of(100L, 101L);
            MockMultipartFile file1 = new MockMultipartFile("multipartFileList", "file1.txt", "text/plain", "content1".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("multipartFileList", "file2.txt", "text/plain", "content2".getBytes());

            doNothing().when(noticeService).putAttachmentFiles(eq(postId), anyList(), anyList());

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, url)
                            .file(file1)
                            .file(file2)
                            .param("postId", String.valueOf(postId))
                            .param("removeAttachmentFileId", "100", "101")
                            .contentType(MediaType.MULTIPART_FORM_DATA));

            //then
            resultActions.andExpect(status().isOk())
                         .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));

            verify(noticeService).putAttachmentFiles(eq(postId), eq(removeAttachmentFileIdList), anyList());

        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failSingleIdFailWrongParameter")
        void updatePostAttachmentFileFail(String testTitle, Long id) throws Exception {

            //given
            List<Long> removeAttachmentFileIdList = List.of(100L, 101L);
            MockMultipartFile file1 = new MockMultipartFile("multipartFileList", "file1.txt", "text/plain", "content1".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("multipartFileList", "file2.txt", "text/plain", "content2".getBytes());

            doNothing().when(noticeService).putAttachmentFiles(eq(id), anyList(), anyList());

            //when
            resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, url)
                    .file(file1)
                    .file(file2)
                    .param("postId", String.valueOf(id))
                    .param("removeAttachmentFileId", "100", "101")
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            //then
            resultActions.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));

        }


    }

    @Nested
    @DisplayName("/board/notice:게시판 삭제")
    class deletePostByIdTest {
        String url = "/board/notice";


        @Test
        @DisplayName("성공: 게시판 삭제")
        void deletePostByIdTestSuccess() throws Exception {
            // given
            Long postId = 1L;

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .param("postId", String.valueOf(postId))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            verify(noticeService, times(1)).deletePostById(postId);
            resultActions
                    .andExpect(status().isOk())  // Check if the status is OK (200)
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.SUCCESS.getReturnCode()));

        }

        @ParameterizedTest(name = "실패-올바르지 않은 파라미터 테스트({0})")
        @MethodSource("com.rsupport.board.util.BoardControllerTestUtil#failSingleIdFailWrongParameter")
        void downloadNoticeAttachmentFileFailWrongParameter(String testTitle, Long id) throws Exception{

            // when
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .param("postId", String.valueOf(id))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            verify(noticeService, times(0)).deletePostById(id);
            resultActions
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.returnCode").value(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode()));
        }


    }


}
