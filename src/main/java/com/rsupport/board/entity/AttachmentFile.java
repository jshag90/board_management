package com.rsupport.board.entity;

import jakarta.persistence.*;
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

    private String fileName; // 파일 이름 저장

    @Lob
    @Column(columnDefinition = "LONGBLOB") // MySQL의 경우 LONGBLOB 사용
    private byte[] fileData; // BLOB 데이터 저장


}
