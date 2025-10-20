package com.niedu.entity.learning_record;

import com.niedu.entity.user.User;
import com.niedu.entity.content.Term;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    public SavedTerm(User user, Term term) {
        this.user = user;
        this.term = term;
    }

    @PrePersist
    protected void onSave() {
        this.savedAt = LocalDateTime.now();
    }
}