package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms_agreement")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
@IdClass(TermsAgreementId.class)
public class TermsAgreement {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    @Column(name = "terms_version", length = 10)
    private String termsVersion;

    @ManyToOne
    @JoinColumn(name = "terms_agree", nullable = false)
    private DefaultType termsAgree;

    @Column(name = "agree_time", length = 20)
    private String agreeTime;

    @Builder
    public TermsAgreement(User user, Terms terms, String termsVersion, 
                        DefaultType termsAgree, String agreeTime) {
        this.user = user;
        this.terms = terms;
        this.termsVersion = termsVersion;
        this.termsAgree = termsAgree;
        this.agreeTime = agreeTime;
    }
}
