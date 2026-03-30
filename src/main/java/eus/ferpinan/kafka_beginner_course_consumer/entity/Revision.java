package eus.ferpinan.kafka_beginner_course_consumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Revision {
    private Long old;
    @JsonProperty("new")
    private Long newRevision;
}
