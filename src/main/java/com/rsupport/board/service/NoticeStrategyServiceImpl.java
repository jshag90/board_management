package com.rsupport.board.service;

import com.rsupport.board.dao.NoticeDao;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.vo.BoardVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service("notice")
@RequiredArgsConstructor
public class NoticeStrategyServiceImpl implements BoardStrategyService {

    private final NoticeDao noticeDao;
    @Override
    @Transactional
    public <T> PostDataDto.SavedPostIdDto savePost(T postVO) {
        BoardVO.RequestSavePost requestSavePost = (BoardVO.RequestSavePost) postVO;
        return noticeDao.saveNoticeInfo(requestSavePost);
    }

    @Override
    @Transactional
    public <T> void savePostAttachmentFiles(Long postId, List<MultipartFile> multipartFileList) throws IOException {
        noticeDao.saveNoticeAttachmentFiles(postId, multipartFileList);
    }

}
