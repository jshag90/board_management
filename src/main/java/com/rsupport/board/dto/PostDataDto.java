package com.rsupport.board.dto;

import lombok.*;

import java.time.LocalDateTime;

public class PostDataDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SavedPostIdDto {
        Long postId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetPostListDto {
        Long id;
        String title;
        boolean isExistAttachmentFiles;
        String createDateTime;
        int hits;
        String writer;
    }


}
