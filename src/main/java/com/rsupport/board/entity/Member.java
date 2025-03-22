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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 125)
    private String loginId;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(length = 125)
    private String username;

    @Column(length = 1)
    private Role role;

    public enum Role {
        ROLE_0, ROLE_1
    }
}
