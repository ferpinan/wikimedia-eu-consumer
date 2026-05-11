# Wikimedia EU Consumer & Stats

A Spring Boot application designed to act as a specialized consumer for filtered Wikimedia event streams. This service
provides dual-functionality: long-term persistence in **MongoDB** for historical analysis and real-time analytics using
**Redis**.

## Core Functionality

1. **Consume:** Listens to the filtered Kafka topic (defaulting to the Basque Wikipedia stream
   `wikimedia.recentchange.eu`).
2. **Idempotent Persistence:** Maps raw JSON payloads to MongoDB entities. It checks for existing `eventId`s before
   saving to prevent data duplication in case of Kafka re-deliveries.
3. **Real-time Analytics:** Computes live statistics using **Redis Pipelining** to ensure high throughput and low
   latency.
4. **Granular Tracking:** Maintains counters and leaderboards (Top Articles/Users) across Daily, Monthly, Yearly, and
   All-time timeframes.

## Technology Stack

* **Java 21**
* **Spring Boot 3.x**
* **Spring Data MongoDB** (Persistence)
* **Spring Data Redis** (High-performance analytics)
* **Spring Kafka** (Event consumption)

## Configuration

The application is fully configurable via environment variables, making it ideal for containerized deployments.

| YAML Property                    | Environment Variable             | Description                      | Default Value                         |
|:---------------------------------|:---------------------------------|:---------------------------------|:--------------------------------------|
| `spring.kafka.bootstrap-servers` | `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka cluster connection string. | `localhost:9092`                      |
| `spring.mongodb.uri`             | `SPRING_MONGODB_URI`             | MongoDB connection string.       | `mongodb://localhost:27017/wikimedia` |
| `spring.data.redis.host`         | `SPRING_DATA_REDIS_HOST`         | Redis server hostname.           | `localhost`                           |
| `spring.data.redis.port`         | `SPRING_DATA_REDIS_PORT`         | Redis server port.               | `6379`                                |
| `wikimedia-eu-consumer.topic`    | `WIKIMEDIA_EU_CONSUMER_TOPIC`    | Kafka topic to consume from.     | `wikimedia.recentchange.eu`           |
| `wikimedia-eu-consumer.group-id` | `WIKIMEDIA_EU_CONSUMER_GROUP_ID` | Kafka consumer group ID.         | `wikimedia-eu-consumer`               |

## Execution Instructions

### Prerequisites

* **Kafka Cluster** accessible by the application.
* **MongoDB** instance for event storage.
* **Redis** server for live stats.
* **Java 21** and **Maven** (for local builds).

### Option A: Local Execution (Development)

1. Clone the repository and update `src/main/resources/application.yml` with your local credentials.
2. Build the project:
   ```bash
   mvn clean package -DskipTests
   ```
3. Run the application:
   ```Bash
   java -jar target/kafka-beginner-course-consumer-0.0.1-SNAPSHOT.jar
   ```
   
### Option B: Docker Deployment

The project includes a configuration optimized for Docker environments.

#### Docker Compose
The following docker-compose.yml defines the deployment environment. It connects to an existing Kafka broker and sets up the required environment variables for Spring Boot.


1. Create docker-compose.yml file:

   ```yaml
   services:
      wikimedia-eu-consumer:
         image: ferpinan/wikimedia-eu-consumer:latest
         container_name: wikimedia-eu-consumer
         environment:
            SPRING_KAFKA_BOOTSTRAP_SERVERS:
            SPRING_MONGODB_URI: 
            SPRING_DATA_REDIS_HOST:
            SPRING_DATA_REDIS_PORT:
            SPRING_DATA_REDIS_PASSWORD:
         restart: unless-stopped
   ```
2. Start the service:
   ```bash
   docker-compose up -d
   ```

## Database Schema & Analytics

### MongoDB (Collection: `wiki_events`)
The system utilizes compound indexes to ensure fast query performance for the following patterns:
*   `wiki` + `timestamp` (Time-series analysis)
*   `user` + `wiki` (User contribution tracking)
*   `title` + `wiki` (Article history)

### Redis (Key Patterns)
*   **Global Counters:** `edit-stats:{granularity}:{date}` (Strings)
*   **User Leaderboards:** `user-ranking:{granularity}:{date}` (Sorted Sets)
*   **Article Rankings:** `article-ranking:{granularity}:{date}` (Sorted Sets)
*   **Recent Activity:** `articles:daily:{date}` (Sorted Set by Timestamp)