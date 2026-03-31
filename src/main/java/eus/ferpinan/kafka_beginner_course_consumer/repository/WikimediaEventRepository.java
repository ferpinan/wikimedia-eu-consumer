package eus.ferpinan.kafka_beginner_course_consumer.repository;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WikimediaEventRepository extends MongoRepository<WikimediaEventEntity, String> {

    Optional<WikimediaEventEntity> findByEventId(Long eventId);

}
