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

    private String title;

    private String content;

    private LocalDateTime exposureStartDateTime;

    private LocalDateTime exposureEndDateTime;

    private LocalDateTime createDateTime;

    private LocalDateTime modifyDateTime;

}
