package com.rsupport.board.service;

import com.rsupport.board.dto.PostDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardStrategyService {

    <T> PostDataDto.SavedPostIdDto savePost(T postVO);

    <T> void savePostAttachmentFiles(Long postIdx, List<MultipartFile> multipartFileList) throws IOException;

}
