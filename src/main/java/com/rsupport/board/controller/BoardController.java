package com.rsupport.board.controller;

import com.rsupport.board.dto.ResponseResultDto;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.service.BoardService;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.utils.FileUtil;
import com.rsupport.board.utils.ReturnCode;
import com.rsupport.board.utils.StringUtil;
import com.rsupport.board.vo.BoardVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final Map<String, BoardService> boardServiceMap;

    @PostMapping(value = "/{type}")
    @Operation(summary = "게시판 글 등록", description = "게시판의 글을 등록합니다.")
    public ResponseEntity<?> savePost(
            @PathVariable("type") BoardTypeEnum boardTypeEnum,
            @Valid @RequestBody BoardVO.RequestSavePost requestSavePost
    ) {

        StringUtil.validOrderStartDateTimeEndDateTime(requestSavePost.getExposureStartDateTime(), requestSavePost.getExposureEndDateTime());

        PostDataDto.SavedPostIdDto savedPostIdDto = boardServiceMap.get(boardTypeEnum.name()).savePost(requestSavePost);

        ResponseResultDto<PostDataDto.SavedPostIdDto> responseResultDto = ResponseResultDto.<PostDataDto.SavedPostIdDto>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .data(savedPostIdDto)
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 첨부파일 등록", description = "게시판의 첨부파일을 등록합니다.")
    @PostMapping(value = "/{type}/attachment-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveAttachmentFiles(
            @PathVariable("type") BoardTypeEnum boardType,
            @RequestParam("postId") @Positive @Min(1) Long postId,
            @RequestPart List<MultipartFile> multipartFileList
    ) throws IOException {

        FileUtil.checkValidFileSize(multipartFileList);

        boardServiceMap.get(boardType.name()).savePostAttachmentFiles(postId, multipartFileList);
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 목록 조회", description = "게시판 목록을 조회합니다.")
    @GetMapping(value = "/{type}/list")
    public ResponseEntity<?> getPostList(
            @PathVariable("type") BoardTypeEnum boardType,
            @ModelAttribute @Valid BoardVO.RequestSearchPostVO requestSearchPostVO
    ) throws ParseException {

        StringUtil.validOrderStartDateEndDate(requestSearchPostVO.getSearchStartCreateDate(), requestSearchPostVO.getSearchEndCreateDate());

        List<PostDataDto.GetPostListDto> postList = boardServiceMap.get(boardType.name()).getPostList(requestSearchPostVO);

        ResponseResultDto<List<PostDataDto.GetPostListDto>> responseResultDto = ResponseResultDto.<List<PostDataDto.GetPostListDto>>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .data(postList)
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 게시글 조회", description = "게시판 게시글을 조회합니다.")
    @GetMapping(value = "/{type}/detail")
    public ResponseEntity<?> getPostData(
            @PathVariable("type") BoardTypeEnum boardType,
            @RequestParam("id") @Valid @Positive @Min(1) Long id
    ) {
        ResponseResultDto<PostDataDto.GetPostDto> responseResultDto = ResponseResultDto.<PostDataDto.GetPostDto>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .data(boardServiceMap.get(boardType.name()).getPostData(id))
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 게시글 첨부파일 다운로드", description = "게시판 게시글 첨부파일을 다운로드 합니다.")
    @GetMapping(value = "/{type}/attachment-file")
    public void downloadAttachmentFile(@PathVariable("type") BoardTypeEnum boardType,
                                       @RequestParam("id") @Valid @Positive @Min(1) Long id,
                                       HttpServletResponse response
    ) {
        boardServiceMap.get(boardType.name()).downloadAttachmentFile(response, id);
    }

    @Operation(summary = "게시판 게시글의 제목, 내용 수정", description = "게시판 게시글 제목, 내용을 수정합니다.")
    @PutMapping(value = "/{type}")
    public ResponseEntity<?> updatePost(@PathVariable("type") BoardTypeEnum boardType,
                     @RequestBody BoardVO.RequestUpdatePostVO requestUpdatePostVO
    ) {
        boardServiceMap.get(boardType.name()).updatePost(requestUpdatePostVO);

        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 첨부파일 목록 수정", description = "게시판 첨부파일들의 목록을 수정합니다.")
    @PutMapping(value = "/{type}/attachment-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> putAttachmentFiles(
            @PathVariable("type") BoardTypeEnum boardType,
            @RequestParam("postId") @Positive Long postId,
            @RequestParam("removeAttachmentFileId") List<Long> removeAttachmentFileIdList,
            @RequestPart List<MultipartFile> multipartFileList
    ) throws IOException {

        boardServiceMap.get(boardType.name()).putAttachmentFiles(postId, removeAttachmentFileIdList, multipartFileList);
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

    @DeleteMapping(value = "/{type}")
    public ResponseEntity<?> deletePostById(
            @PathVariable("type") BoardTypeEnum boardType,
            @RequestParam("postId") @Positive Long postId
    ){

        boardServiceMap.get(boardType.name()).deletePostById(postId);
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SUCCESS.getReturnCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.SUCCESS.getHttpStatus());
    }

}
