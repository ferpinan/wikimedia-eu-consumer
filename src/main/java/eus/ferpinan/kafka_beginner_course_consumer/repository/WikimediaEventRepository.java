package eus.ferpinan.kafka_beginner_course_consumer.repository;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for {@link WikimediaEventEntity} providing abstraction for
 * MongoDB persistence operations.
 * <p>
 * This interface leverages Spring Data MongoDB to provide standard CRUD operations
 * and custom query execution against the "wiki_events" collection.
 * </p>
 */
public interface WikimediaEventRepository extends MongoRepository<WikimediaEventEntity, String> {

    /**
     * Retrieves a specific Wikimedia event based on its original EventStream ID.
     * <p>
     * This is useful for checking the existence of an event or preventing
     * duplicates using the business ID rather than the internal MongoDB Object ID.
     * </p>
     *
     * @param eventId The original ID of the event as provided by Wikimedia.
     * @return An {@link Optional} containing the found entity, or empty if no
     *         event matches the given ID.
     */
    Optional<WikimediaEventEntity> findByEventId(Long eventId);

}