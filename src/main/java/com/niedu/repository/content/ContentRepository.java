package com.niedu.repository.content;

import com.niedu.entity.content.Content;
import com.niedu.entity.course.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByStep(Step step);

    @Query("""
            select scq.referenceAnswer
            from SentenceCompletionQuiz scq
            where scq.step.id = :stepId
              and scq.id = :contentId
            """)
    Optional<String> findSentenceCompletionReferenceAnswer(
            @Param("stepId") Long stepId,
            @Param("contentId") Long contentId
    );
}
