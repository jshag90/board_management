package com.rsupport.board.service;

import com.rsupport.board.dao.MemberDao;
import com.rsupport.board.dao.NoticeDao;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.utils.FileUtil;
import com.rsupport.board.vo.BoardVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service("notice")
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements BoardService {

    private final NoticeDao noticeDao;
    private final MemberDao memberDao;
    @Override
    @Transactional
    public <T> PostDataDto.SavedPostIdDto savePost(T postVO) {
        BoardVO.RequestSavePost requestSavePost = (BoardVO.RequestSavePost) postVO;
        requestSavePost.setWriter(memberDao.getUserNameByAdminRole()); //temp user
        log.info(requestSavePost.toString());
        return noticeDao.saveNoticeInfo(requestSavePost);
    }

    @Override
    @Transactional
    public <T> void savePostAttachmentFiles(Long postId, List<MultipartFile> multipartFileList) throws IOException {
        noticeDao.saveNoticeAttachmentFiles(postId, multipartFileList);
    }

    @Override
    public <T> List<PostDataDto.GetPostListDto> getPostList(T requestSearchPostVO) throws ParseException {
        return noticeDao.getNoticeList((BoardVO.RequestSearchPostVO) requestSearchPostVO);
    }

    @Override
    @Transactional
    public <T> PostDataDto.GetPostDto getPostData(Long id) {
        noticeDao.updateNoticeHits(id);
        return noticeDao.getNoticePost(id);
    }

    @Override
    public void downloadAttachmentFile(HttpServletResponse response, Long id) {
        PostDataDto.AttachmentFileDataDto attachmentFileDataDto = noticeDao.downloadAttachmentFile(id);
        byte[] fileData = attachmentFileDataDto.getFileData();
        FileUtil.responseFileDownload(response, attachmentFileDataDto.getFileName(), fileData);
    }

    @Override
    @Transactional
    public void updatePost(BoardVO.RequestUpdatePostVO requestUpdatePostVO) {
        noticeDao.updateNotice(requestUpdatePostVO);
    }

    @Override
    @Transactional
    public void putAttachmentFiles(Long postId, List<Long> removeAttachmentFileIdList, List<MultipartFile> multipartFileList) throws IOException {
        noticeDao.deleteAttachmentFile(postId, removeAttachmentFileIdList);
        if(multipartFileList != null && !multipartFileList.isEmpty()) {
            noticeDao.saveNoticeAttachmentFiles(postId, multipartFileList);
        }
    }

    @Override
    @Transactional
    public void deletePostById(Long postId) {
        noticeDao.deleteNoticeById(postId);
    }


}
