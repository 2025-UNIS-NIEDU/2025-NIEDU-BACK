package com.niedu.repository.home;

import com.niedu.entity.home.HomeNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeNewsRepository extends JpaRepository<HomeNews, Long> {

    @Query("SELECT n FROM HomeNews n WHERE n.publishedDate = CURRENT_DATE ORDER BY n.id DESC")
    List<HomeNews> findTodayNews();
}
