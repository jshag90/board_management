package com.rsupport.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_post_attachment_board_post", columnList = "boardTypeId, postId")
})
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
    @JoinColumn(name = "boardTypeId")
    private BoardType boardTypeId;

    @ManyToOne(targetEntity = AttachmentFile.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "attachmentFileId")
    private AttachmentFile attachmentFileId;

}
