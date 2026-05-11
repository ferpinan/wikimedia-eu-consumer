package eus.ferpinan.kafka_beginner_course_consumer.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for transforming raw JSON event strings into
 * {@link WikimediaEventEntity} domain objects.
 * <p>
 * This mapper handles the specific structure of Wikimedia EventStreams, where the
 * core event information is typically encapsulated within a "data" field of the
 * incoming JSON envelope.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class WikimediaEventMapper {

    private final ObjectMapper objectMapper;

    /**
     * Converts a raw JSON event string into a {@link WikimediaEventEntity}.
     * <p>
     * @param event The raw JSON string received from the Kafka topic.
     * @return A populated {@link WikimediaEventEntity} instance.
     * @throws JsonProcessingException If the input string is not valid JSON or
     *                                 cannot be mapped to the target entity.
     */
    public WikimediaEventEntity toEntity(String event) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(event);
        String eventData = root.path("data").asText();
        return objectMapper.readValue(eventData, WikimediaEventEntity.class);
    }
}