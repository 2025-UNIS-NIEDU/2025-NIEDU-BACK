package com.niedu.repository.learning_record;

import com.niedu.entity.content.Term;
import com.niedu.entity.learning_record.SavedTerm;
import com.niedu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SavedTermRepository extends JpaRepository<SavedTerm, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    long deleteByUserAndTerm(User user, Term term);

    boolean existsByUserAndTerm(User user, Term term);

    @Transactional(readOnly = true)
    List<SavedTerm> findByUser_IdOrderByTerm_NameAsc(Long userId);

    @Transactional(readOnly = true)
    List<SavedTerm> findByUser_IdOrderBySavedAtDesc(Long userId);
}
