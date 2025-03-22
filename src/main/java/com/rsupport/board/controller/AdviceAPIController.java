package com.rsupport.board.controller;

import com.rsupport.board.dto.ResponseResultDto;
import com.rsupport.board.exception.CustomException;
import com.rsupport.board.utils.ReturnCode;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceAPIController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SERVER_ERROR.getReturnCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseResultDto.toString());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {

        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(e.getReturnCode().getReturnCode())
                .message(e.getReturnCode().getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), e.getReturnCode().getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SERVER_ERROR.getReturnCode())
                .message(ReturnCode.SERVER_ERROR.getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, ReturnCode.SERVER_ERROR.getHttpStatus());
    }


}
