package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "redis_sign_in_count")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RedisSignInCount {

    @Id
    @Column(name = "user_id", length = 20)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    private String result; // success or fail

    @Column(name = "count")
    private Integer count;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

}
