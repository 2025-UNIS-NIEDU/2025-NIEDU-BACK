package com.niedu.repository.topic;

import com.niedu.entity.topic.UserTopicPreference;
import com.niedu.entity.topic.UserTopicPreferenceId;
import com.niedu.entity.user.User;
import com.niedu.entity.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicPreferenceRepository extends JpaRepository<UserTopicPreference, UserTopicPreferenceId> {
}
