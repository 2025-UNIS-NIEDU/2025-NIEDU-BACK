package com.niedu.repository.topic;

import com.niedu.entity.topic.SubTopic;
import com.niedu.entity.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Long> {
    SubTopic findByName(String subtopic);
    boolean existsByTopicAndName(Topic topic, String name);
}
