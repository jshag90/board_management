package com.rsupport.board.service;

import com.rsupport.board.dao.MemberDao;
import com.rsupport.board.dao.NoticeDao;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.vo.BoardVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    BoardVO.RequestSavePost requestSavePost;
    @InjectMocks
    private NoticeServiceImpl noticeService;
    @Mock
    private MemberDao memberDao;
    @Mock
    private NoticeDao noticeDao;

    @BeforeEach
    void setUp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        requestSavePost = BoardVO.RequestSavePost
                .builder()
                .title("this is test title.")
                .content("this is test content")
                .exposureStartDateTime(LocalDateTime.parse("2025-03-18 09:00:00", formatter))
                .exposureEndDateTime(LocalDateTime.parse("2025-03-18 11:00:00", formatter))
                .build();

    }

    @Test
    void savePostAttachmentFiles() {
    }

    @Test
    void getPostList() {
    }

    @Test
    void getPostData() {
    }

    @Test
    void downloadAttachmentFile() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void putAttachmentFiles() {
    }

    @Test
    void deletePostById() {
    }

    @Nested
    @DisplayName("공지사항 등록 서비스 클래스 테스트")
    class SaveNoticeServiceTest {

        @Test
        @DisplayName("공지사항 등록 성공 테스트")
        void saveNoticeServiceSuccess() {
            // Given
            when(memberDao.getUserNameByAdminRole()).thenReturn("admin");
            when(noticeDao.saveNoticeInfo(requestSavePost)).thenReturn(PostDataDto.SavedPostIdDto.builder()
                                                                                                .postId(1L)
                                                                                                .build());

            // When
            PostDataDto.SavedPostIdDto result = noticeService.savePost(requestSavePost);

            // Then
            assertNotNull(result);
            verify(memberDao, times(1)).getUserNameByAdminRole();
            verify(noticeDao, times(1)).saveNoticeInfo(requestSavePost);
        }
    }
}