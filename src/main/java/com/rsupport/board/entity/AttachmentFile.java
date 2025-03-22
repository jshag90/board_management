package com.rsupport.board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AttachmentFile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 260, nullable = false) // 파일 이름 최대 길이 260자 제한
    private String fileName; // 파일 이름 저장

    @Lob
    @Column(columnDefinition = "LONGBLOB") // MySQL의 경우 LONGBLOB 사용
    private byte[] fileData; // BLOB 데이터 저장


}
