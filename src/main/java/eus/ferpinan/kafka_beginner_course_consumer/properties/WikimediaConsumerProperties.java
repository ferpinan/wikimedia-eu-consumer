package eus.ferpinan.kafka_beginner_course_consumer.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "wikimedia-eu-consumer")
public class WikimediaConsumerProperties {

    /**
     * List of Wikipedia namespaces to exclude from statistics.
     */
    private List<String> excludedNamespaces;
}