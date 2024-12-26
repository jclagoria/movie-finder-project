# Tmdb Service Search

### Overview

This project implements a Spring Boot application for the **TMDB Service Search** for consume.
the endpoint provided by TMDB.
It provides a REST API to fetch movie data from on database, in this case mongodb. 
The application is built with reactive programming using Spring WebFlux


### Prerequisites
- **Java Version**: 21
- **Maven Version**: 3.9+
- **Spring Boot Version**: 3.4.1

### Dependencies
The application uses the following dependencies:
- **Spring Boot Starter WebFlux**: For reactive REST API development.
- - **Spring Booty Data Mongodb Reactive**: For manage the connection and work with MongoDB instance.
- **SpringDoc OpenAPI**: For Swagger UI documentation.
- **Reactor Test**: For unit testing reactive streams.
- **Spring Boot Starter Test**: For testing.
- **De flapdoodle Embed Mongo**: For testing the database.

### How to Build and Run
1. Clone the Repository:
   ```bash
    git clone https://github.com/jclagoria/movies-challenge-api.git
    cd registration-user-api
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
    - Swagger UI: http://localhost:8082/swagger-ui.html

### Testing the Application
#### Using Swagger UI
1. Start the application.
2. Open http://localhost:8082/swagger-ui.html.

