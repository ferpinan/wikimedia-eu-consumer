package eus.ferpinan.kafka_beginner_course_consumer.consumer;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import eus.ferpinan.kafka_beginner_course_consumer.mapper.WikimediaEventMapper;
import eus.ferpinan.kafka_beginner_course_consumer.repository.WikimediaEventRepository;
import eus.ferpinan.kafka_beginner_course_consumer.service.WikimediaStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WikimediaEuConsumer {

    private final WikimediaEventRepository repository;
    private final WikimediaEventMapper mapper;
    private final WikimediaStatsService wikimediaStatsService;

    @KafkaListener(
            topics = "wikimedia.recentchange.eu",
            groupId = "wikimedia-eu-consumer"
    )
    public void consume(String message) {
        log.debug("Consuming message from wikimedia.recentchange.eu topic: {}", message);
        try {
            WikimediaEventEntity wikimediaEvent = mapper.toEntity(message);
            log.info("Guardando en DB: {}", repository.getClass().getName());
            WikimediaEventEntity savedEvent = repository.save(wikimediaEvent);
            log.info("Saved at mongo with id {}", savedEvent.getMongoId());
            log.info("Guardado: [{}] {} - {} with id {}", wikimediaEvent.getType(), wikimediaEvent.getWiki(), wikimediaEvent.getTitle(), savedEvent.getMongoId());
            wikimediaStatsService.updateStats(wikimediaEvent);
        } catch (Exception e) {
            log.error("Error procesando mensaje: {}", e.getMessage());
        }
    }
}