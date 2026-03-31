package eus.ferpinan.kafka_beginner_course_consumer.service;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import eus.ferpinan.kafka_beginner_course_consumer.repository.WikimediaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WikimediaEventService {

    private final WikimediaEventRepository wikimediaEventRepository;

    public WikimediaEventEntity save(WikimediaEventEntity wikimediaEventEntity){
        Optional<WikimediaEventEntity> byEventId = wikimediaEventRepository.findByEventId(wikimediaEventEntity.getEventId());
        return byEventId.orElse(wikimediaEventRepository.save(wikimediaEventEntity));
    }

}
