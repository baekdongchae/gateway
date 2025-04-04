package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notice_type")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
public class NoticeType {

    @Id
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "type_name", length = 20)
    private String typeName;

    @Builder
    public NoticeType(Integer typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }
}
