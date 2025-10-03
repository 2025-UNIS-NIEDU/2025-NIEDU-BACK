package com.niedu.entity.topic;

import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class UserTopicPreferenceId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "topic_id")
    private Integer topicId;
}

@Entity
@Table(name = "user_topic_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTopicPreference {

    @EmbeddedId
    private UserTopicPreferenceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("topicId")
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public UserTopicPreference(User user, Topic topic) {
        this.id = new UserTopicPreferenceId(user.getId(), topic.getId());
        this.user = user;
        this.topic = topic;
    }
}