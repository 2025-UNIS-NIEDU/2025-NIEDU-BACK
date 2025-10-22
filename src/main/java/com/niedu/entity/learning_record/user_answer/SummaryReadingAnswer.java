package com.niedu.entity.learning_record.user_answer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.content.KeywordContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("SUMMARY")
public class SummaryReadingAnswer extends UserAnswer {
    @ElementCollection
    @CollectionTable(
            name = "summary_keywords",
            joinColumns = @JoinColumn(name = "summary_id")
    )
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
}
