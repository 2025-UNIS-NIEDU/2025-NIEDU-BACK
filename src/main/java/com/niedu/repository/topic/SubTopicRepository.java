package com.niedu.repository.topic;

import com.niedu.entity.topic.SubTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Long> {
    SubTopic findByName(String subtopic);
}
