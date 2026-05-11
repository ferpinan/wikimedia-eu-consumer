package eus.ferpinan.kafka_beginner_course_consumer.service;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import eus.ferpinan.kafka_beginner_course_consumer.repository.WikimediaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for managing the persistence of Wikimedia events.
 * <p>
 * This service acts as an abstraction layer between the Kafka consumer and the
 * MongoDB repository, ensuring that events are stored correctly and providing
 * basic deduplication logic based on the Wikimedia event ID.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WikimediaEventService {

    private final WikimediaEventRepository wikimediaEventRepository;

    /**
     * Persists a Wikimedia event entity into MongoDB.
     * <p>
     * Before saving, the method checks if an event with the same {@code eventId}
     * already exists in the database. If it exists, the existing record is
     * returned; otherwise, a new record is created. This provides a level of
     * idempotency to handle potential duplicate messages from Kafka.
     * </p>
     *
     * @param wikimediaEventEntity The event entity to be saved.
     */
    public void save(WikimediaEventEntity wikimediaEventEntity){
        Optional<WikimediaEventEntity> byEventId = wikimediaEventRepository.findByEventId(wikimediaEventEntity.getEventId());

        // If the event exists, we use it; otherwise, we save the new one.
        WikimediaEventEntity savedWikimediaEventEntity = byEventId.orElseGet(() -> wikimediaEventRepository.save(wikimediaEventEntity));

        log.info("Saved Wikimedia Event Entity in Mongo: [{}] {} - {} with id {}",
                savedWikimediaEventEntity.getType(),
                savedWikimediaEventEntity.getWiki(),
                savedWikimediaEventEntity.getTitle(),
                savedWikimediaEventEntity.getMongoId());
    }

}