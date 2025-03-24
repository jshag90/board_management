package com.rsupport.board.service;

import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.vo.BoardVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface BoardService {

    /**
     * 게시글 제목, 내용을 저장합니다.
     * @param postVO
     * @return
     * @param <T>
     */
    <T> PostDataDto.SavedPostIdDto savePost(T postVO);

    /**
     * 게시글 첨부파일을 추가합니다.
     * @param postIdx
     * @param multipartFileList
     * @param <T>
     * @throws IOException
     */
    <T> void savePostAttachmentFiles(Long postIdx, List<MultipartFile> multipartFileList) throws IOException;

    /**
     * 게시판 목록을 조회합니다.
     * @param requestSearchPostVO
     * @return
     * @param <T>
     * @throws ParseException
     */
    <T> List<PostDataDto.GetPostListDto> getPostList(T requestSearchPostVO) throws ParseException;

    /**
     * 특정 게시물 내용을 조회합니다.
     * @param id
     * @return
     * @param <T>
     */
    <T> PostDataDto.GetPostDto getPostData(Long id);

    /**
     * 첨부파일을 다운로드합니다.
     * @param response
     * @param id
     */
    void downloadAttachmentFile(HttpServletResponse response, Long id);

    /**
     * 게시글 내용을 수정합니다.
     * @param requestUpdatePostVO
     */
    void updatePost(BoardVO.RequestUpdatePostVO requestUpdatePostVO);

    /**
     * 게시글 첨부파일 목록을 수정합니다.
     * @param postId
     * @param removeAttachmentFileIdList
     * @param multipartFileList
     * @throws IOException
     */
    void putAttachmentFiles(Long postId, List<Long> removeAttachmentFileIdList, List<MultipartFile> multipartFileList) throws IOException;

    /**
     * 특정 게시글을 제거합니다.
     * @param postId
     */
    void deletePostById(Long postId);
}
