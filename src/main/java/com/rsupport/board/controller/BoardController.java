package com.rsupport.board.controller;

import com.rsupport.board.dto.ResponseResultDto;
import com.rsupport.board.service.BoardStrategyService;
import com.rsupport.board.utils.ErrorCode;
import com.rsupport.board.vo.BoardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final Map<String, BoardStrategyService> boardServiceMap;

    @PostMapping("/{type}")
    public ResponseEntity<?> insertPost(
            @PathVariable("type") String boardType,
            @RequestPart("postData") BoardVO.RequestInsertPost requestInsertPost,
            @RequestParam("files") List<MultipartFile> attachmentFiles
    ) {

        Long insertedId = boardServiceMap.get(boardType).insertPost();

        ResponseResultDto<Long> responseResultDto = ResponseResultDto.<Long>builder().errorCode(ErrorCode.SUCCESS).data(insertedId).build();
        return new ResponseEntity<>(responseResultDto
                , new HttpHeaders(), HttpStatus.OK);

    }

}
