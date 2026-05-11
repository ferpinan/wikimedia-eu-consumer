package eus.ferpinan.kafka_beginner_course_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Wikimedia Kafka Consumer application.
 * <p>
 * This class initializes the Spring Boot application context, enabling auto-configuration,
 * component scanning, and the starting of the Kafka listeners defined within the project.
 * </p>
 */
@SpringBootApplication
public class EuWikimediaConsumerApplication {

	/**
	 * Main method used to launch the application.
	 *
	 * @param args Command line arguments passed to the application at startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(EuWikimediaConsumerApplication.class, args);
	}
}