package eus.ferpinan.kafka_beginner_course_consumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the character length details of a Wikimedia page revision.
 * <p>
 * This class provides a comparison of the page size (in bytes/characters)
 * before and after the edit was performed.
 * </p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Length {

    /**
     * The total length of the page in characters prior to the current change.
     * May be null for new page creations.
     */
    private Integer old;

    /**
     * The total length of the page in characters after the current change.
     * <p>
     * Mapped from the JSON property "new", which is a reserved keyword in Java.
     * </p>
     */
    @JsonProperty("new")
    private Integer newLength;
}