package com.finflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * FINFLOW APPLICATION ENTRY POINT
 * ============================================================================
 * What is this class?
 * The main launch pad for the FinFlow Spring Boot application.
 *
 * Annotations explained:
 * - @SpringBootApplication: A 3-in-1 convenience meta-annotation that bundles:
 *    1. @Configuration: Tags the class as a source of bean definitions for the application context.
 *    2. @EnableAutoConfiguration: Tells Spring Boot to automatically configure beans based on classpath dependencies
 *       (e.g., detecting spring-boot-starter-web automatically starts an embedded Tomcat server on port 8080).
 *    3. @ComponentScan: Scans this package ('com.finflow') and all sub-packages for Spring components
 *       annotated with @Component, @Service, @Repository, @RestController, and registers them as beans.
 *
 * Python/FastAPI Comparison:
 * In FastAPI, you typically launch the server via Uvicorn:
 *   uvicorn main:app --reload
 * In Spring Boot, running public static void main(String[] args) launches the entire embedded server!
 */
@SpringBootApplication
public class FinFlowApplication {

    public static void main(String[] args) {
        System.out.println(">>> [FinFlow] Bootstrapping Spring Boot Application Context...");
        SpringApplication.run(FinFlowApplication.class, args);
        System.out.println(">>> [FinFlow] Spring Boot Backend is UP and listening on http://localhost:8080");
    }
}
