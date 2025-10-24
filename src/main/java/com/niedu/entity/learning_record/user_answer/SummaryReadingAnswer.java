package com.niedu.entity.learning_record.user_answer;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("SUMMARY")
@SuperBuilder
public class SummaryReadingAnswer extends UserAnswer {
    @ElementCollection
    @CollectionTable(
            name = "summary_keywords",
            joinColumns = @JoinColumn(name = "summary_id")
    )

    @Builder.Default
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
}
