package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms_versions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
@IdClass(TermsVersionId.class)
public class TermsVersion {

    @Id
    @ManyToOne
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    @Id
    @Column(name = "terms_version", length = 10)
    private String termsVersion;

    @Column(name = "terms_content", columnDefinition = "text")
    private String termsContent;

    @Column(name = "create_time", length = 20)
    private String createTime;

    @Builder
    public TermsVersion(Terms terms, String termsVersion, String termsContent, String createTime) {
        this.terms = terms;
        this.termsVersion = termsVersion;
        this.termsContent = termsContent;
        this.createTime = createTime;
    }
}
