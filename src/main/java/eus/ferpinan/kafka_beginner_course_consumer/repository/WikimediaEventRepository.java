package eus.ferpinan.kafka_beginner_course_consumer.repository;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WikimediaEventRepository extends MongoRepository<WikimediaEventEntity, String> {
}
