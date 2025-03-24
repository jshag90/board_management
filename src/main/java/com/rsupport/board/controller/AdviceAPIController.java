package com.rsupport.board.controller;

import com.rsupport.board.dto.ResponseResultDto;
import com.rsupport.board.exception.CustomException;
import com.rsupport.board.utils.ReturnCode;
import jakarta.validation.UnexpectedTypeException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class AdviceAPIController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.TOO_BIG_SIZE_FILE.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.TOO_BIG_SIZE_FILE.getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchExceptions(MethodArgumentTypeMismatchException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartExceptions(MissingServletRequestPartException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<?> handleUnexpectedTypeExceptions(UnexpectedTypeException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handlerMethodValidationException(HandlerMethodValidationException ex) {
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.INVALID_REQUEST_PARAMETER.getReturnCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseResultDto, new HttpHeaders(), ReturnCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
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
        e.printStackTrace();
        ResponseResultDto<Void> responseResultDto = ResponseResultDto.<Void>builder()
                .returnCode(ReturnCode.SERVER_ERROR.getReturnCode())
                .message(ReturnCode.SERVER_ERROR.getMessage())
                .build();

        return new ResponseEntity<>(responseResultDto, ReturnCode.SERVER_ERROR.getHttpStatus());
    }


}
