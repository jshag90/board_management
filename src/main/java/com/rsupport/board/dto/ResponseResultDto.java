package com.rsupport.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Schema(description = "에러 응답 DTO")
public class ResponseResultDto<T> {

    @Schema(description = "응답 코드 (0: 성공, -1: 서버 에러, -2: 유효하지 않은 파라미터, -3: 파일 용량 초과)", example = "0")
    Integer returnCode;

    @Schema(description = "오류 메시지")
    String message;

    @Schema(description = "추가 데이터")
    T data;
}
