package eus.ferpinan.kafka_beginner_course_consumer.service;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class WikimediaStatsService {

    private final StringRedisTemplate redis;

    public void updateStats(WikimediaEventEntity event) {
        if(event.getTitle().startsWith("Lankide:")){
            return;
        }
        String date = Instant.ofEpochSecond(event.getTimestamp())
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
                .toString(); // "2026-03-30"
        String month = date.substring(0, 7); // "2026-03"
        String year = date.substring(0, 4);  // "2026"

        // Contadores
        redis.opsForValue().increment("edit-stats:daily:" + date);
        redis.opsForValue().increment("edit-stats:monthly:" + month);
        redis.opsForValue().increment("edit-stats:yearly:" + year);
        redis.opsForValue().increment("edit-stats:alltime");

        // Rankings de usuarios
        if (event.getUser() != null && !event.getBot()) {
            redis.opsForZSet().incrementScore("user-ranking:daily:" + date, event.getUser(), 1);
            redis.opsForZSet().incrementScore("user-ranking:monthly:" + month, event.getUser(), 1);
            redis.opsForZSet().incrementScore("user-ranking:yearly:" + year, event.getUser(), 1);
            redis.opsForZSet().incrementScore("user-ranking:alltime", event.getUser(), 1);
        }

        // Rankings de artículos
        if (event.getTitle() != null) {
            redis.opsForZSet().incrementScore("article-ranking:daily:" + date, event.getTitle(), 1);
            redis.opsForZSet().incrementScore("article-ranking:monthly:" + month, event.getTitle(), 1);
            redis.opsForZSet().incrementScore("article-ranking:yearly:" + year, event.getTitle(), 1);
            redis.opsForZSet().incrementScore("article-ranking:alltime", event.getTitle(), 1);
        }

        // Lista de artículos del día
        if (event.getTitle() != null) {
            redis.opsForZSet().add(
                    "articles:daily:" + date,
                    event.getTitle(),
                    event.getTimestamp()  // score = timestamp, así están ordenados por tiempo
            );
        }
    }
}
