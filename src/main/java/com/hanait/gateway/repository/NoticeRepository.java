package com.hanait.gateway.repository;

import com.hanait.gateway.model.Notice;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("""
        SELECT n FROM Notice n
        JOIN NoticeType t ON n.noticeType = t
        JOIN DefaultType d ON n.noticeVisibility = d
        WHERE t.typeName = :typeName AND d.typeName = 'true'
        """)
    List<Notice> findVisibleNoticesByType(@Param("typeName") String typeName);

    @Query("""
        SELECT n FROM Notice n
        JOIN n.noticeType t
        JOIN n.noticeVisibility d
        WHERE t.typeName = :typeName
          AND d.typeName = 'true'
          AND (
            n.noticeEndDate IS NULL OR
            n.noticeEndDate > CURRENT_DATE
          )
        """)
    Page<Notice> findPagedVisibleNoticesByType(@Param("typeName") String typeName, Pageable pageable);
}


