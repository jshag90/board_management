package com.rsupport.board.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReturnCode {
      SUCCESS(0, "요청 처리에 성공하였습니다.", HttpStatus.OK)
    , SERVER_ERROR(-1, "서버에러", HttpStatus.INTERNAL_SERVER_ERROR)
    , INVALID_REQUEST_PARAMETER(-2, "유효하지 않은 파라미터", HttpStatus.BAD_REQUEST)
    ;


    private final int returnCode;
    private final String message;
    private final HttpStatus httpStatus;
}
