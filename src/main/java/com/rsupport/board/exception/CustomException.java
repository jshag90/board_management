package com.rsupport.board.exception;

import com.rsupport.board.utils.ReturnCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
    private final ReturnCode returnCode;
}
