package com.niedu.repository.content;

import com.niedu.entity.content.Term;
import com.niedu.entity.course.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findAllBySession(Session session);
}
