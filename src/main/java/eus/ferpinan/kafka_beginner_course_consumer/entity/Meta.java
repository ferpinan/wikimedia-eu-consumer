package eus.ferpinan.kafka_beginner_course_consumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the metadata associated with a Wikimedia event.
 * <p>
 * This class captures technical details provided by the Wikimedia EventStream API,
 * including unique identifiers, timestamps, and Kafka-related information such as
 * topic names and offsets.
 * </p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {

    /**
     * The unique URI identifying the specific event resource.
     */
    private String uri;

    /**
     * The unique ID of the request that generated this event.
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * The unique ID of the event itself.
     */
    private String id;

    /**
     * The domain from which the event originated (e.g., "www.wikipedia.org").
     */
    private String domain;

    /**
     * The name of the stream this event belongs to.
     */
    private String stream;

    /**
     * The ISO-8601 timestamp (Date/Time) of the event.
     */
    private String dt;

    /**
     * The name of the source Kafka topic.
     */
    private String topic;

    /**
     * The partition number within the Kafka topic where this event was stored.
     */
    private Integer partition;

    /**
     * The sequence offset of the message within the Kafka partition.
     */
    private Long offset;
}