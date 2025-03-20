package com.rsupport.board.service;

import com.rsupport.board.dao.NoticeDao;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.utils.FileUtil;
import com.rsupport.board.vo.BoardVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service("notice")
@RequiredArgsConstructor
public class NoticeServiceImpl implements BoardService {

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

    @Override
    public <T> List<PostDataDto.GetPostListDto> getPostList(T requestSearchPostVO) {
        return noticeDao.getNoticeList((BoardVO.RequestSearchPostVO) requestSearchPostVO);
    }

    @Override
    @Transactional
    public <T> PostDataDto.GetPostDto getPostData(Long id) {
        return noticeDao.getNoticePost(id);
    }

    @Override
    public void downloadAttachmentFile(HttpServletResponse response, Long id) {
        PostDataDto.AttachmentFileDataDto attachmentFileDataDto = noticeDao.downloadAttachmentFile(id);
        byte[] fileData = attachmentFileDataDto.getFileData();
        FileUtil.responseFileDownload(response, attachmentFileDataDto.getFileName(), fileData);
    }

    @Override
    public void updatePost(BoardVO.RequestUpdatePostVO requestUpdatePostVO) {
        noticeDao.updateNotice(requestUpdatePostVO);
    }


}
