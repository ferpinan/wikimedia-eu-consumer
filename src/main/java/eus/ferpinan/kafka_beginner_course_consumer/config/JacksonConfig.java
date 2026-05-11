package eus.ferpinan.kafka_beginner_course_consumer.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for customizing the Jackson {@link ObjectMapper}
 * instance within the Spring application context.
 * <p>
 * This setup ensures that the JSON processing behavior is consistent across
 * the Kafka consumer services.
 * </p>
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures a primary {@link ObjectMapper} bean.
     * <p>
     * The mapper is configured to ignore unknown JSON properties during deserialization
     * to prevent exceptions when the incoming Kafka message contains fields not
     * mapped in the target DTO (Data Transfer Object).
     * </p>
     * * @return a configured {@link ObjectMapper} instance with
     * {@code FAIL_ON_UNKNOWN_PROPERTIES} set to {@code false}.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
