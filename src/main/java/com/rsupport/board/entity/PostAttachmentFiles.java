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
public class PostAttachmentFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Long postIdx;

    @ManyToOne(targetEntity = BoardType.class, fetch = FetchType.LAZY)
    @JoinColumn(name="boardTypeIdx")
    private BoardType boardTypeIdx;

    @ManyToOne(targetEntity = AttachmentFiles.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "attachmentFilesIdx")
    private AttachmentFiles attachmentFilesIdx;

}
