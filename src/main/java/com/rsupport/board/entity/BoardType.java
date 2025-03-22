package com.rsupport.board.entity;

import com.rsupport.board.utils.BoardTypeEnum;
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
public class BoardType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false) // 최대 길이 10자로 제한
    private BoardTypeEnum name;

}
