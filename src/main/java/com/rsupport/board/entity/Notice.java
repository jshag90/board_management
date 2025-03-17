package com.rsupport.board.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private Long idx;

    private String title;

    private String content;

}
