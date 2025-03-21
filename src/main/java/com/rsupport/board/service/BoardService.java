package com.rsupport.board.service;

import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.vo.BoardVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardService {

    <T> PostDataDto.SavedPostIdDto savePost(T postVO);

    <T> void savePostAttachmentFiles(Long postIdx, List<MultipartFile> multipartFileList) throws IOException;

    <T> List<PostDataDto.GetPostListDto> getPostList(T requestSearchPostVO);

    <T> PostDataDto.GetPostDto getPostData(Long id);

    void downloadAttachmentFile(HttpServletResponse response, Long id);

    void updatePost(BoardVO.RequestUpdatePostVO requestUpdatePostVO);

    void putAttachmentFiles(Long postId, List<Long> removeAttachmentFileIdList, List<MultipartFile> multipartFileList) throws IOException;

    void deletePostById(Long postId);
}
