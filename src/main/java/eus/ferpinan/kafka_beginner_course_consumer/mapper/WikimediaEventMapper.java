package eus.ferpinan.kafka_beginner_course_consumer.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikimediaEventMapper {

    private final ObjectMapper objectMapper;

    public WikimediaEventEntity toEntity(String event) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(event);
        String eventData = root.path("data").asText();
        return objectMapper.readValue(eventData, WikimediaEventEntity.class);
    }
}
