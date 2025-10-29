package com.niedu.repository.content;

import com.niedu.entity.content.NewsRef;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRefRepository extends JpaRepository<NewsRef, Long> {

    List<NewsRef> findAllByOrderByIdDesc(Pageable pageable);
}