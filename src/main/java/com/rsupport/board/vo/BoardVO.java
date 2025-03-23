package com.rsupport.board.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rsupport.board.utils.SearchTypeEnum;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BoardVO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class RequestSavePost {

        @NotBlank
        String title;

        @NotBlank
        String content;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull
        LocalDateTime exposureStartDateTime; //공지(노출) 시작 일시

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull
        LocalDateTime exposureEndDateTime; //공지(노출) 종료 일시

        @JsonIgnore
        String writer;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @EqualsAndHashCode
    @ToString
    public static class RequestSearchPostVO {

        SearchTypeEnum searchType;

        String searchWord;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate searchStartCreateDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate searchEndCreateDate;

        @Positive
        @Min(1)
        int pageSize;

        @Positive
        @Min(1)
        int page;

        @JsonIgnore
        public int getOffset() {
            return (getPage() - 1) * getPageSize();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class RequestUpdatePostVO {

        @Positive
        @Min(1)
        Long id;


        @NotBlank
        String title;

        @NotBlank
        String content;

    }


}
