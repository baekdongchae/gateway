package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fhir_users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FhirUser {

    @Id
    @Column(name = "user_code")
    private Long userCode;

    @OneToOne
    @JoinColumn(name = "user_code")
    private User user;

    @Builder
    public FhirUser(User user) {
        this.userCode = user.getUserCode();
        this.user = user;
    }
}
