package com.hanait.gateway.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponse {
    private Long noticeId;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime createTime; // 일반 공지용
}