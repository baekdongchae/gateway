package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notice extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "notice_title", columnDefinition = "text")
    private String noticeTitle;

    @Column(name = "notice_content", columnDefinition = "text")
    private String noticeContent;

    @ManyToOne
    @JoinColumn(name = "notice_type", nullable = false)
    private NoticeType noticeType;

    @Column(name = "notice_end_date")
    private LocalDateTime noticeEndDate;

    @ManyToOne
    @JoinColumn(name = "notice_visibility", nullable = false)
    private DefaultType noticeVisibility;

//    @Column(name = "update_time", length = 20)
//    private String updateTime;
//
//    @Column(name = "create_time", length = 20)
//    private String createTime;

    @Builder
    public Notice(String noticeTitle, String noticeContent, NoticeType noticeType,
                  LocalDateTime noticeEndDate, DefaultType noticeVisibility) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeType = noticeType;
        this.noticeEndDate = noticeEndDate;
        this.noticeVisibility = noticeVisibility;
    }
}
