package com.rsupport.board.controller;

import com.rsupport.board.dto.ResponseResultDto;
import com.rsupport.board.dto.PostDataDto;
import com.rsupport.board.service.BoardStrategyService;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.utils.ErrorCode;
import com.rsupport.board.vo.BoardVO;
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

    private final Map<String, BoardStrategyService> boardServiceMap;

    @PostMapping(value = "/{type}")
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

}
