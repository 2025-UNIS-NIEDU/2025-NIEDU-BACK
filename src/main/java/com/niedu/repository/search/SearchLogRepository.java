package com.niedu.repository.search;

import com.niedu.entity.search.SearchLog;
import com.niedu.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    Page<SearchLog> findByUserOrderBySearchedAtDesc(User user, Pageable pageable);

    @Query("SELECT s.keyword FROM SearchLog s GROUP BY s.keyword ORDER BY COUNT(s.keyword) DESC")
    List<String> findPopularKeywords(Pageable pageable);

}