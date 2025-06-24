package com.hanait.gateway.controller;

import com.hanait.gateway.model.dto.NoticeResponse;
import com.hanait.gateway.model.dto.PagedResponse;
import com.hanait.gateway.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoticeController.class)
@AutoConfigureMockMvc
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @Test
    void 공지_리스트_페이징_조회_성공() throws Exception {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<NoticeResponse> mockNotices = List.of(
                new NoticeResponse(1L, "제목1", "내용1", LocalDateTime.parse("2025-06-19 10:00", formatter)),
                new NoticeResponse(2L, "제목2", "내용2", LocalDateTime.parse("2025-06-19 09:00", formatter))
        );

        PagedResponse<NoticeResponse> mockPagedResponse = new PagedResponse<>(
                mockNotices,
                new PagedResponse.Pagination(0, 1, 2, 10, true)
        );

        when(noticeService.getDefaultNotices(any())).thenReturn(
                new PageImpl<>(mockNotices)
        );

        // when & then
        mockMvc.perform(get("/notice/list")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data.items.length()").value(2))
                .andExpect(jsonPath("$.data.data.items[0].noticeTitle").value("제목1"))
                .andExpect(jsonPath("$.data.data.pagination.totalElements").value(2));
    }
}