package com.niedu.entity.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.content.KeywordContent;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("SUMMARY")
public class SummaryReading extends Content {
    @Lob
    @Column(nullable = false)
    private String summary;

    @Column(columnDefinition = "jsonb")
    private String keywordsJson;

    // --- 직렬화 도우미 메서드 --- //

    public void setKeywords(List<KeywordContent> keywords) {
        try {
            this.keywordsJson = new ObjectMapper().writeValueAsString(keywords);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize keywords", e);
        }
    }

    public List<KeywordContent> getKeywords() {
        if (keywordsJson == null || keywordsJson.isBlank()) return new ArrayList<>();
        try {
            return new ObjectMapper().readValue(keywordsJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

}
