package com.rsupport.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Notice {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false) // 제목 최대 255자
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime exposureStartDateTime;

    private LocalDateTime exposureEndDateTime;

    private LocalDateTime createDateTime;

    private LocalDateTime modifyDateTime;

    @Column(length = 5, nullable = false, columnDefinition = "int default 0")
    private Integer hits;

    @Column(length = 100, nullable = false) // 작성자 이름 최대 100자
    private String writer;

}
