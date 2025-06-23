package org.example.smartlawgt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.example.smartlawgt.query.repositories")
@EnableScheduling
public class SmartLawGtApplication {
    /**
     * Main method to run the SmartLaw GT application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartLawGtApplication.class, args);
    }

}
