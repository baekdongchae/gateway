package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipient_caregiver")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
@IdClass(RecipientCaregiverId.class)
public class RecipientCaregiver extends BaseTimeEntity{

    @Id
    @ManyToOne
    @JoinColumn(name = "caregiver", nullable = false)
    private User caregiver;

    @Id
    @ManyToOne
    @JoinColumn(name = "recipient", nullable = false)
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "active")
    private DefaultType active;

//    @Column(name = "update_time", length = 20)
//    private String updateTime;
//
//    @Column(name = "create_time", length = 20)
//    private String createTime;

    @Builder
    public RecipientCaregiver(User caregiver, User recipient, DefaultType active) {
        this.caregiver = caregiver;
        this.recipient = recipient;
        this.active = active;
    }
}
