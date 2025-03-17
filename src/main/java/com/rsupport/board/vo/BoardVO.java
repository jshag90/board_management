package com.rsupport.board.vo;

import lombok.*;

import java.time.LocalDateTime;

public class BoardVO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestInsertPost{
        String title;
        String content;
        LocalDateTime exposureStartDateTime; //공지 시작 일시
        LocalDateTime exposureEndDateTime; //공지 종료 일시
    }
}
