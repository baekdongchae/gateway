package com.hanait.gateway.model;

import com.hanait.gateway.config.jwt.dto.member.MemberInfoDto;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, length = 50)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String email, String password, String username, Role role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public MemberInfoDto toMemberInfoDte() {
        return MemberInfoDto.builder()
                .email(this.getEmail())
                .username(this.getUsername())
                .role(this.getRole())
                .build();
    }


}
