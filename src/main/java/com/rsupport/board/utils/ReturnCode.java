package com.rsupport.board.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    SUCCESS(0, "요청 처리에 성공하였습니다.", HttpStatus.OK)
    , NO_INVALID_ORDER_LOCAL_DATE_TIME(-2, "시작일시, 종료일시가 순서가 올바르지 않습니다.", HttpStatus.BAD_REQUEST)
    , SERVER_ERROR(-1, "서버에러", HttpStatus.INTERNAL_SERVER_ERROR)

    ;


    private final int returnCode;
    private final String message;
    private final HttpStatus httpStatus;
}
