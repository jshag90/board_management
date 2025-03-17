package com.rsupport.board.dto;

import com.rsupport.board.utils.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class ResponseResultDto<T> implements Serializable {
    ErrorCode errorCode;
    T data;
}
