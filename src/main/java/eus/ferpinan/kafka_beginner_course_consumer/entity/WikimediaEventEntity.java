package eus.ferpinan.kafka_beginner_course_consumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "wiki_events")
@CompoundIndex(name = "idx_wiki_day", def = "{'wiki': 1, 'timestamp': -1}")
@CompoundIndex(name = "idx_user_wiki", def = "{'user': 1, 'wiki': 1}")
@CompoundIndex(name = "idx_title_wiki", def = "{'title': 1, 'wiki': 1}")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikimediaEventEntity {

    @Id
    @JsonIgnore
    private String mongoId;

    @JsonProperty("id")
    private Long eventId;

    @JsonProperty("$schema")
    private String schema;

    private Meta meta;

    private String type;
    private Integer namespace;
    private String title;

    @JsonProperty("title_url")
    private String titleUrl;

    private String comment;

    @JsonProperty("parsedcomment")
    private String parsedComment;

    private Long timestamp;
    private String user;
    private Boolean bot;
    private Boolean minor;
    private Boolean patrolled;

    @JsonProperty("notify_url")
    private String notifyUrl;

    @JsonProperty("server_url")
    private String serverUrl;

    @JsonProperty("server_name")
    private String serverName;

    @JsonProperty("server_script_path")
    private String serverScriptPath;

    private String wiki;
    private Length length;
    private Revision revision;

    @JsonProperty("log_id")
    private Integer logId;

    @JsonProperty("log_type")
    private String logType;

    @JsonProperty("log_action")
    private String logAction;

    @JsonProperty("log_params")
    private Object logParams;

    @JsonProperty("log_action_comment")
    private String logActionComment;
}
