# ReceiptServer

This project is a Java-based Spring Boot application that processes receipts using OCR and natural language processing. It uses Gradle as the build tool.

## Features

- Extracts items from receipt images using OCR.
- Processes receipt data using a large language model.
- Exposes REST endpoints for receipt processing.
- Swagger API documentation is available for exploring the API.

## Technologies

- Java
- Spring Boot
- Gradle
- PostgreSQL / H2 (for testing)

## Setup

1. Clone the repository.
2. Configure your environment in the `application.properties` and `application-test.properties`.
3. Build the project using Gradle.
4. Run the application with your preferred IDE (e.g., IntelliJ IDEA).

## API Documentation

The Swagger API specifications can be viewed at:  
[https://soksok.io/api/swagger-ui/index.html](https://soksok.io/api/swagger-ui/index.html)

## Testing

The project uses an H2 in-memory database for integration tests. Adjust the configuration in `src/test/resources/application-test.properties` as needed.

## License

All Rights Reserved. Use of this project without explicit permission is prohibited.
