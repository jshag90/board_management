package com.rsupport.board.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS(0, "요청 처리에 성공하였습니다.");

    private final int returnCode;
    private final String message;
}
