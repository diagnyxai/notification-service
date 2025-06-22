# Notification Service

Multi-channel notification service for the Diagnyx platform, supporting email, SMS, push notifications, and webhooks.

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Messaging**: RabbitMQ (optional)
- **Port**: 8081 (HTTP)

## Features

- Multi-channel notification delivery
- Template-based notifications
- Delivery tracking and status updates
- Notification preferences
- Scheduled notifications

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/notifications` | POST | Send notification |
| `/api/v1/notifications/{id}` | GET | Get notification status |
| `/api/v1/notifications/templates` | GET | List templates |
| `/api/v1/notifications/templates/{id}` | GET | Get template |
| `/api/v1/notifications/preferences/{userId}` | GET | Get user preferences |
| `/api/v1/notifications/preferences/{userId}` | PUT | Update user preferences |

## Notification Channels

- Email
- SMS
- Push notifications
- Webhooks
- In-app notifications

## Running Locally

```bash
# Build the service
mvn clean package

# Run the service
java -jar target/notification-service.jar
```

## Database Configuration

Configure database connection in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dgx-dev
    username: dev
    password: dev
```

## Health Check

```
http://localhost:8081/health
``` 