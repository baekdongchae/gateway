package com.hanait.gateway.service;

import com.hanait.gateway.model.dto.NoticeResponse;
import com.hanait.gateway.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponse> getPopupNotices() {
        return noticeRepository.findVisibleNoticesByType("popup").stream()
                .map(n -> new NoticeResponse(
                        n.getNoticeId(),
                        n.getNoticeTitle(),
                        n.getNoticeContent(),
                        null // 팝업은 createTime 필요 없음
                ))
                .collect(Collectors.toList());
    }

    public Page<NoticeResponse> getDefaultNotices(Pageable pageable) {
        return noticeRepository.findPagedVisibleNoticesByType("default", pageable)
                .map(n -> new NoticeResponse(
                        n.getNoticeId(),
                        n.getNoticeTitle(),
                        n.getNoticeContent(),
                        n.getCreateTime()
                ));
    }

}
