package com.hanait.gateway.controller;

import com.hanait.gateway.config.jwt.token.ApiResponseJson;
import com.hanait.gateway.model.dto.NoticeResponse;
import com.hanait.gateway.model.dto.PagedResponse;
import com.hanait.gateway.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/popup")
    public ApiResponseJson getPopupNotices() {
        List<NoticeResponse> popupNotices = noticeService.getPopupNotices();
        return new ApiResponseJson(HttpStatus.OK, Map.of("success", true, "data", popupNotices));
    }

    @GetMapping("/list")
    public ApiResponseJson getDefaultNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<NoticeResponse> pageResult = noticeService.getDefaultNotices(pageable);

        return new ApiResponseJson(HttpStatus.OK, Map.of(
                "success", true,
                "data", PagedResponse.from(pageResult)
        ));
    }
}