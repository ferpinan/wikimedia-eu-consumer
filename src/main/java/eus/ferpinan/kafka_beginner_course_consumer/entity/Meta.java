package eus.ferpinan.kafka_beginner_course_consumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    private String uri;
    @JsonProperty("request_id")
    private String requestId;
    private String id;
    private String domain;
    private String stream;
    private String dt;
    private String topic;
    private Integer partition;
    private Long offset;
}
