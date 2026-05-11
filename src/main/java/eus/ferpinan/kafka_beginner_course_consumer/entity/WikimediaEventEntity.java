package eus.ferpinan.kafka_beginner_course_consumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data model representing a Wikimedia "recent change" event, persisted in MongoDB.
 * <p>
 * This entity captures metadata, user information, and revision details for
 * edits made across various Wikimedia projects (Wikipedia, Wikidata, etc.).
 * It is configured to ignore unknown JSON properties to ensure compatibility
 * with evolving upstream schema changes.
 * </p>
 *
 * <h3>Database Indexes:</h3>
 * <ul>
 *     <li><b>idx_wiki_day:</b> Optimizes queries for changes within a specific wiki sorted by time.</li>
 *     <li><b>idx_user_wiki:</b> Optimizes searches for contributions by a specific user on a specific wiki.</li>
 *     <li><b>idx_title_wiki:</b> Optimizes searches for the history of a specific page title.</li>
 * </ul>
 */
@Data
@Document(collection = "wiki_events")
@CompoundIndex(name = "idx_wiki_day", def = "{'wiki': 1, 'timestamp': -1}")
@CompoundIndex(name = "idx_user_wiki", def = "{'user': 1, 'wiki': 1}")
@CompoundIndex(name = "idx_title_wiki", def = "{'title': 1, 'wiki': 1}")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikimediaEventEntity {

    /**
     * Internal MongoDB unique identifier.
     * Ignored during JSON serialization/deserialization.
     */
    @Id
    @JsonIgnore
    private String mongoId;

    /**
     * The original event ID provided by the Wikimedia EventStream.
     */
    @JsonProperty("id")
    private Long eventId;

    /**
     * The URI identifying the JSON schema for this event.
     */
    @JsonProperty("$schema")
    private String schema;

    /**
     * Metadata associated with the event (request ID, domain, etc.).
     */
    private Meta meta;

    /**
     * Type of event (e.g., "edit", "new", "log", "categorize").
     */
    private String type;

    /**
     * ID of the namespace the page belongs to.
     */
    private Integer namespace;

    /**
     * Full title of the page that was changed.
     */
    private String title;

    /**
     * Direct URL to the page title.
     */
    @JsonProperty("title_url")
    private String titleUrl;

    /**
     * Edit summary or log comment.
     */
    private String comment;

    /**
     * The comment parsed into HTML.
     */
    @JsonProperty("parsedcomment")
    private String parsedComment;

    /**
     * Unix timestamp of the event.
     */
    private Long timestamp;

    /**
     * Username or IP address of the editor.
     */
    private String user;

    /**
     * Indicates if the edit was made by a registered bot.
     */
    private Boolean bot;

    /**
     * Indicates if the edit was marked as a minor change.
     */
    private Boolean minor;

    /**
     * Status indicating if the change has been patrolled (for wikis with patrolling enabled).
     */
    private Boolean patrolled;

    /**
     * URL for change notifications.
     */
    @JsonProperty("notify_url")
    private String notifyUrl;

    /**
     * The base URL of the server (e.g., "https://en.wikipedia.org").
     */
    @JsonProperty("server_url")
    private String serverUrl;

    /**
     * The specific name of the server (e.g., "en.wikipedia.org").
     */
    @JsonProperty("server_name")
    private String serverName;

    /**
     * The internal path to the MediaWiki scripts.
     */
    @JsonProperty("server_script_path")
    private String serverScriptPath;

    /**
     * The database name of the wiki (e.g., "enwiki", "eswiki").
     */
    private String wiki;

    /**
     * Length details of the revision.
     */
    private Length length;

    /**
     * Information regarding the old and new revision IDs.
     */
    private Revision revision;

    /**
     * ID of the log entry (only for log events).
     */
    @JsonProperty("log_id")
    private Integer logId;

    /**
     * Type of log entry (e.g., "delete", "block", "move").
     */
    @JsonProperty("log_type")
    private String logType;

    /**
     * Specific action taken in the log.
     */
    @JsonProperty("log_action")
    private String logAction;

    /**
     * Structured parameters for the log action.
     */
    @JsonProperty("log_params")
    private Object logParams;

    /**
     * Formatted comment explaining the log action.
     */
    @JsonProperty("log_action_comment")
    private String logActionComment;
}