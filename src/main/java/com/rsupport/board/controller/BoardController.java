package com.rsupport.board.controller;

import com.rsupport.board.dto.ResponseResultDto;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.service.BoardService;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.utils.ErrorCode;
import com.rsupport.board.vo.BoardVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @RequestBody @Valid BoardVO.RequestSavePost requestSavePost
    ) {

        PostDataDto.SavedPostIdDto savedPostIdDto = boardServiceMap.get(boardTypeEnum.name()).savePost(requestSavePost);

        ResponseResultDto<PostDataDto.SavedPostIdDto> responseResultDto = ResponseResultDto.<PostDataDto.SavedPostIdDto>builder()
                .returnCode(ErrorCode.SUCCESS.getReturnCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(savedPostIdDto)
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ErrorCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 첨부파일 등록", description = "게시판의 첨부파일을 등록합니다.")
    @PostMapping(value = "/{type}/attachment-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveAttachmentFiles(
            @PathVariable("type") BoardTypeEnum boardType,
            @RequestParam("postId") @Positive Long postId,
            @RequestPart List<MultipartFile> multipartFileList
    ) throws IOException {

        boardServiceMap.get(boardType.name()).savePostAttachmentFiles(postId, multipartFileList);
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ErrorCode.SUCCESS.getReturnCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ErrorCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 목록 조회", description = "게시판 목록을 조회합니다.")
    @GetMapping(value = "/{type}/list")
    public ResponseEntity<?> getPostList(
            @PathVariable("type") BoardTypeEnum boardType,
            @ModelAttribute BoardVO.RequestSearchPostVO requestSearchPostVO
    ) {
        log.info(requestSearchPostVO.toString());
        List<PostDataDto.GetPostListDto> postList = boardServiceMap.get(boardType.name()).getPostList(requestSearchPostVO);

        ResponseResultDto<List<PostDataDto.GetPostListDto>> responseResultDto = ResponseResultDto.<List<PostDataDto.GetPostListDto>>builder()
                .returnCode(ErrorCode.SUCCESS.getReturnCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(postList)
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ErrorCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 게시글 조회", description = "게시판 게시글을 조회합니다.")
    @GetMapping(value = "/{type}/detail")
    public ResponseEntity<?> getPostData(
            @PathVariable("type") BoardTypeEnum boardType,
            @RequestParam("id") Long id
    ) {
        ResponseResultDto<PostDataDto.GetPostDto> responseResultDto = ResponseResultDto.<PostDataDto.GetPostDto>builder()
                .returnCode(ErrorCode.SUCCESS.getReturnCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(boardServiceMap.get(boardType.name()).getPostData(id))
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ErrorCode.SUCCESS.getHttpStatus());
    }

    @Operation(summary = "게시판 게시글 첨부파일 다운로드", description = "게시판 게시글 첨부파일을 다운로드 합니다.")
    @GetMapping(value = "/{type}/attachment-file")
    public void downloadAttachmentFile(@PathVariable("type") BoardTypeEnum boardType,
                                       @RequestParam("id") Long id,
                                       HttpServletResponse response
    ) {
        boardServiceMap.get(boardType.name()).downloadAttachmentFile(response, id);
    }

    @Operation(summary = "게시판 게시글을 제목, 내용 수정", description = "게시판 게시글 제목, 내용을 수정합니다.")
    @PutMapping(value = "/{type}")
    public ResponseEntity<?> updatePost(@PathVariable("type") BoardTypeEnum boardType,
                     @RequestBody BoardVO.RequestUpdatePostVO requestUpdatePostVO
    ) {
        boardServiceMap.get(boardType.name()).updatePost(requestUpdatePostVO);

        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ErrorCode.SUCCESS.getReturnCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ErrorCode.SUCCESS.getHttpStatus());
    }


}
