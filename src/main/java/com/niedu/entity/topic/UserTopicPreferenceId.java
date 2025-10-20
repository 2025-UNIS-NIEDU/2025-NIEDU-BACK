package com.niedu.entity.topic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTopicPreferenceId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "topic_id")
    private Integer topicId;
}
