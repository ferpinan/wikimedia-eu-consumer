package eus.ferpinan.kafka_beginner_course_consumer.service;

import eus.ferpinan.kafka_beginner_course_consumer.entity.WikimediaEventEntity;
import eus.ferpinan.kafka_beginner_course_consumer.properties.WikimediaConsumerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for computing and persisting real-time statistics in Redis.
 * <p>
 * This service tracks global edit counters, user rankings, and article rankings
 * across different time granularities (daily, monthly, yearly, and all-time).
 * It uses Redis Pipelining to optimize network performance.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WikimediaStatsService {

    private final StringRedisTemplate redis;
    private final WikimediaConsumerProperties wikimediaConsumerProperties;

    /**
     * Thread-safe formatter for UTC dates.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC);

    // Redis key prefixes
    private static final String KEY_EDIT_STATS = "edit-stats";
    private static final String KEY_USER_RANKING = "user-ranking";
    private static final String KEY_ARTICLE_RANKING = "article-ranking";
    private static final String KEY_ARTICLES_DAILY = "articles";

    // Time granularity suffixes
    private static final String DAILY = "daily";
    private static final String MONTHLY = "monthly";
    private static final String YEARLY = "yearly";
    private static final String ALLTIME = "alltime";

    // TTL configurations
    private static final Duration TTL_DAILY = Duration.ofDays(90);
    private static final Duration TTL_MONTHLY = Duration.ofDays(365);
    private static final Duration TTL_YEARLY = Duration.ofDays(730);

    /**
     * Updates multiple Redis counters and Sorted Sets based on the incoming event.
     * <p>
     * The method performs the following:
     * <ul>
     *     <li>Filters out user-page edits (titles starting with "Lankide:").</li>
     *     <li>Increments global edit counters for the day, month, year, and total.</li>
     *     <li>Updates User Leaderboards (ZSet) based on edit frequency (excluding bots).</li>
     *     <li>Updates Article Leaderboards (ZSet) based on edit frequency.</li>
     *     <li>Maintains a chronological record of articles edited today.</li>
     * </ul>
     * All operations are wrapped in a pipeline to minimize network round-trips.
     * </p>
     *
     * @param event The Wikimedia event containing edit details.
     */
    public void updateStats(WikimediaEventEntity event) {
        // 1. Validate event data - Only process main namespace articles
        if (event.getTitle() == null || isSpecialNamespace(event.getTitle())) {
            log.debug("Skipping event: special namespace or null title - {}", event.getTitle());
            return;
        }

        if (event.getTimestamp() == null) {
            log.warn("Skipping event with null timestamp for article: {}", event.getTitle());
            return;
        }

        // 2. Prepare temporal dimensions
        String date = DATE_FORMATTER.format(Instant.ofEpochSecond(event.getTimestamp()));
        String month = date.substring(0, 7); // Format: yyyy-MM
        String year = date.substring(0, 4);  // Format: yyyy

        // 3. Execute in Pipeline (Reduces network overhead from N calls to 1)
        redis.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {

            // Global edit counters
            String dailyKey = buildKey(KEY_EDIT_STATS, DAILY, date);
            String monthlyKey = buildKey(KEY_EDIT_STATS, MONTHLY, month);
            String yearlyKey = buildKey(KEY_EDIT_STATS, YEARLY, year);
            String alltimeKey = buildKey(KEY_EDIT_STATS, ALLTIME);

            incrementValue(dailyKey);
            incrementValue(monthlyKey);
            incrementValue(yearlyKey);
            incrementValue(alltimeKey);

            // Set TTL for time-bound keys
            redis.expire(dailyKey, TTL_DAILY);
            redis.expire(monthlyKey, TTL_MONTHLY);
            redis.expire(yearlyKey, TTL_YEARLY);

            // User Rankings (Leaderboards) - Ignore bots for fairness
            if (event.getUser() != null && Boolean.FALSE.equals(event.getBot())) {
                String userDailyKey = buildKey(KEY_USER_RANKING, DAILY, date);
                String userMonthlyKey = buildKey(KEY_USER_RANKING, MONTHLY, month);
                String userYearlyKey = buildKey(KEY_USER_RANKING, YEARLY, year);
                String userAlltimeKey = buildKey(KEY_USER_RANKING, ALLTIME);

                incrementZSet(userDailyKey, event.getUser());
                incrementZSet(userMonthlyKey, event.getUser());
                incrementZSet(userYearlyKey, event.getUser());
                incrementZSet(userAlltimeKey, event.getUser());

                // Set TTL for user rankings
                redis.expire(userDailyKey, TTL_DAILY);
                redis.expire(userMonthlyKey, TTL_MONTHLY);
                redis.expire(userYearlyKey, TTL_YEARLY);
            }

            // Article Rankings (Trending articles)
            String articleDailyKey = buildKey(KEY_ARTICLE_RANKING, DAILY, date);
            String articleMonthlyKey = buildKey(KEY_ARTICLE_RANKING, MONTHLY, month);
            String articleYearlyKey = buildKey(KEY_ARTICLE_RANKING, YEARLY, year);
            String articleAlltimeKey = buildKey(KEY_ARTICLE_RANKING, ALLTIME);

            incrementZSet(articleDailyKey, event.getTitle());
            incrementZSet(articleMonthlyKey, event.getTitle());
            incrementZSet(articleYearlyKey, event.getTitle());
            incrementZSet(articleAlltimeKey, event.getTitle());

            // Set TTL for article rankings
            redis.expire(articleDailyKey, TTL_DAILY);
            redis.expire(articleMonthlyKey, TTL_MONTHLY);
            redis.expire(articleYearlyKey, TTL_YEARLY);

            // Daily Activity List: Chronologically ordered using timestamp as score
            String articlesKey = buildKey(KEY_ARTICLES_DAILY, DAILY, date);
            addToZSet(articlesKey, event.getTitle(), event.getTimestamp());
            redis.expire(articlesKey, TTL_DAILY);

            return null;
        });

        log.debug("Updated stats for article: {}, user: {}", event.getTitle(), event.getUser());
    }

    /**
     * Checks if a title belongs to a special namespace (non-article content).
     *
     * @param title The article title to check.
     * @return true if the title is a special namespace, false if it's a main article.
     */
    private boolean isSpecialNamespace(String title) {
        return wikimediaConsumerProperties.getExcludedNamespaces().stream()
                .anyMatch(namespace -> title.startsWith(namespace + ":"));
    }

    /**
     * Builds a Redis key from components.
     *
     * @param components Variable number of key components to join.
     * @return The concatenated Redis key.
     */
    private String buildKey(String... components) {
        return String.join(":", components);
    }

    /**
     * Increments a simple key-value counter.
     *
     * @param key The Redis key.
     */
    private void incrementValue(String key) {
        redis.opsForValue().increment(key);
    }

    /**
     * Increments the score of a member in a Sorted Set by 1.
     *
     * @param key    The Redis key for the Sorted Set.
     * @param member The member (user or article title) to increment.
     */
    private void incrementZSet(String key, String member) {
        redis.opsForZSet().incrementScore(key, member, 1);
    }

    /**
     * Adds a member to a Sorted Set or updates its score.
     *
     * @param key    The Redis key for the Sorted Set.
     * @param member The member to add.
     * @param score  The score value (used here for chronological sorting).
     */
    private void addToZSet(String key, String member, double score) {
        redis.opsForZSet().add(key, member, score);
    }
}