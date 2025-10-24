package com.niedu.entity.learning_record.user_answer;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SIMPLE")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SimpleAnswer extends UserAnswer {
    @Column
    private String value;
}
