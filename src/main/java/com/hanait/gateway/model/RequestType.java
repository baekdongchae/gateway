package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "request_type")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "type_name", length = 20)
    private String typeName;

    @Builder
    public RequestType(String typeName) {
        this.typeName = typeName;
    }
}
