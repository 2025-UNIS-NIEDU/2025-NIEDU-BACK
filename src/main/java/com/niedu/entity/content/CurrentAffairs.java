package com.niedu.entity.content;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("CURRENT")
@SuperBuilder
public class CurrentAffairs extends Content {
    @Column(nullable = false)
    private String issue;

    @Column(nullable = false)
    private String cause;

    @Column(nullable = false)
    String circumstance;

    @Column(nullable = false)
    private String result;

    @Column(nullable = false)
    private String effect;
}
