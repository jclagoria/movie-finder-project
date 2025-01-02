# Api Services Movies Back End

### Overview

This project implements a Spring Boot application for the **Api Service Movies**.
It provides a REST API to fetch movie data from an external API and return the list of movies. 
The application is built with reactive programming using Spring WebFlux.


### Prerequisites
- **Java Version**: 21
- **Maven Version**: 3.9+
- **Spring Boot Version**: 3.4.1

### Dependencies
The application uses the following dependencies:
- **Spring Boot Starter WebFlux**: For reactive REST API development.
- **SpringDoc OpenAPI**: For Swagger UI documentation.
- **Reactor Test**: For unit testing reactive streams.
- **Spring Boot Starter Test**: For testing.

### How to Build and Run
1. Clone the Repository:
   ```bash
    git clone https://github.com/jclagoria/movie-finder-project.git
    cd movie-finder-project
    cd api-service-movies
   ```
2. Build the Application:
   ```bash
    mvn clean install
   ```
3. Run the Application:
   ```bash
    mvn spring-boot:run
   ```
4. Access the Application:
    - Swagger UI: http://localhost:8081/swagger-ui.html

### Testing the Application
#### Using Swagger UI
1. Start the application.
2. Open http://localhost:8081/swagger-ui.html.

