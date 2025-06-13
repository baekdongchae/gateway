package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "redis_sign_in_block")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RedisSignInBlock {

    @Id
    @Column(name = "user_id", length = 20)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    private LocalDateTime blockStartTime;

    private long blockDurationMinutes;

    private String reason;


}
