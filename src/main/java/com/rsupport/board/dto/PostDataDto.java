package com.rsupport.board.dto;

import lombok.*;

import java.util.List;
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
        Boolean isExistAttachmentFiles;
        String createDateTime;
        String exposureStartDateTime;
        String exposureEndDateTime;
        int hits;
        String writer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetPostDto {
        Long id;
        String title;
        String content;
        String createDateTime;
        int hits;
        String writer;
        List<GetAttachmentFileDto> attachmentFileNameList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetAttachmentFileDto {
        Long id;
        String fileName;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AttachmentFileDataDto {
        String fileName;
        byte[] fileData;
    }

}
