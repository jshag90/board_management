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
public class PostAttachmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    @ManyToOne(targetEntity = BoardType.class, fetch = FetchType.LAZY)
    @JoinColumn(name="boardTypeId")
    private BoardType boardTypeId;

    @ManyToOne(targetEntity = AttachmentFile.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "attachmentFileId")
    private AttachmentFile attachmentFileId;

}
