package com.niedu.repository.content;

import com.niedu.entity.content.NewsRef;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRefRepository extends JpaRepository<NewsRef, Long> {
    @Query(
            value = """
            SELECT * FROM news_ref
            WHERE published_at = (
                SELECT MAX(published_at) FROM news_ref
            )
            ORDER BY RANDOM()
            LIMIT 2
            """,
            nativeQuery = true
    )
    List<NewsRef> findTwoRandomFromLatestDate();
}