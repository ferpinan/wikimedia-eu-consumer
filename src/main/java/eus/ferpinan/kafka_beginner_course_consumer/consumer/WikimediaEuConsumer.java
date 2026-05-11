package eus.ferpinan.kafka_beginner_course_consumer.consumer;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import eus.ferpinan.kafka_beginner_course_consumer.mapper.WikimediaEventMapper;
import eus.ferpinan.kafka_beginner_course_consumer.service.WikimediaEventService;
import eus.ferpinan.kafka_beginner_course_consumer.service.WikimediaStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service responsible for processing Wikimedia recent change events.
 * <p>
 * This service listens to a specific Kafka topic, maps the incoming raw JSON messages
 * into domain entities, persists them to the database, and updates real-time statistics.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WikimediaEuConsumer {

    private final WikimediaEventService wikimediaEventService;
    private final WikimediaEventMapper wikimediaEventMapper;
    private final WikimediaStatsService wikimediaStatsService;

    /**
     * Kafka listener method that triggers whenever a new message is published to the
     * monitored Wikimedia topic.
     * <p>
     * The process involves:
     * <ol>
     *     <li>Mapping the raw string message to a {@link WikimediaEventEntity}.</li>
     *     <li>Saving the entity via {@link WikimediaEventService}.</li>
     *     <li>Updating event statistics via {@link WikimediaStatsService}.</li>
     * </ol>
     * Errors during processing are caught and logged to prevent the consumer
     * from failing or entering an infinite retry loop depending on the error handler policy.
     * </p>
     *
     * @param message The raw message received from the Kafka topic.
     */
    @KafkaListener(
            topics = "${wikimedia-eu-consumer.topic:wikimedia.recentchange.eu}",
            groupId = "${wikimedia-eu-consumer.group-id:wikimedia-eu-consumer}"
    )
    public void consume(String message) {
        log.debug("Consuming message: {}", message);
        try {
            WikimediaEventEntity wikimediaEvent = wikimediaEventMapper.toEntity(message);
            wikimediaEventService.save(wikimediaEvent);
            wikimediaStatsService.updateStats(wikimediaEvent);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }
}