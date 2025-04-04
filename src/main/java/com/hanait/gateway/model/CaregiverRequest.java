package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "caregiver_request")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CaregiverRequest extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "caregiver")
    private User caregiver;

    @ManyToOne
    @JoinColumn(name = "state")
    private RequestType state;

//    @Column(name = "update_time", length = 20)
//    private String updateTime;
//
//    @Column(name = "create_time", length = 20)
//    private String createTime;

    @Builder
    public CaregiverRequest(User user, User caregiver, RequestType state) {
        this.user = user;
        this.caregiver = caregiver;
        this.state = state;
    }
}
