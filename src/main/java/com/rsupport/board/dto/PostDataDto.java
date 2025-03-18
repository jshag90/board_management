package com.rsupport.board.dto;

import lombok.*;
public class PostDataDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SavedPostIdDto {
        Long postId;

    }
}
