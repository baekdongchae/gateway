package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fhir_health_data")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FhirHealthData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_code")
    private User user;

    @Builder
    public FhirHealthData(User user) {
        this.user = user;
    }
}
