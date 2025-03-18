package com.rsupport.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class ResponseResultDto<T> {
    Integer returnCode;
    String message;
    T data;
}
