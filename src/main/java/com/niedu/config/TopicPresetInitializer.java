package com.niedu.config;

import com.niedu.entity.topic.SubTopic;
import com.niedu.entity.topic.Topic;
import com.niedu.repository.topic.SubTopicRepository;
import com.niedu.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TopicPresetInitializer implements ApplicationRunner {

    private final TopicRepository topicRepository;
    private final SubTopicRepository subTopicRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, List<String>> presets = new LinkedHashMap<>();
        presets.put("정치", List.of("대통령실", "국회", "정당", "북한", "행정", "국방", "외교"));
        presets.put("경제", List.of("금융", "증권", "산업", "중소기업", "부동산"));
        presets.put("사회", List.of("사건", "교육", "노동", "환경", "의료", "법", "젠더"));
        presets.put("국제", List.of("미국", "중남미", "유럽", "중동", "중국", "일본"));

        for (Map.Entry<String, List<String>> entry : presets.entrySet()) {
            String topicName = entry.getKey();
            Topic topic = topicRepository.findByName(topicName);
            if (topic == null) {
                topic = topicRepository.save(new Topic(topicName));
            }

            for (String subTopicName : entry.getValue()) {
                if (!subTopicRepository.existsByTopicAndName(topic, subTopicName)) {
                    subTopicRepository.save(new SubTopic(topic, subTopicName));
                }
            }
        }
    }
}
