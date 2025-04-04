package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_id")
    private Integer termsId;

    @Column(name = "terms_name", columnDefinition = "text")
    private String termsName;

    @Column(name = "terms_content", columnDefinition = "text")
    private String termsContent;

    @ManyToOne
    @JoinColumn(name = "terms_essential", nullable = false)
    private DefaultType termsEssential;

    @Column(name = "terms_version", length = 10)
    private String termsVersion;

    @Column(name = "update_time", length = 20)
    private String updateTime;

    @Builder
    public Terms(String termsName, String termsContent, DefaultType termsEssential,
                String termsVersion, String updateTime) {
        this.termsName = termsName;
        this.termsContent = termsContent;
        this.termsEssential = termsEssential;
        this.termsVersion = termsVersion;
        this.updateTime = updateTime;
    }
}
