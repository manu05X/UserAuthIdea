# UserAuthIdea


# Idea Collaboration Platform

A Spring Boot application that enables employees to brainstorm, vote, and collaborate on ideas within a company.

## Features

- **User Authentication**: Secure login using employee ID with JWT tokens
- **Idea Management**:
    - Create, view, update, and delete ideas
    - Add tags to ideas
- **Voting System**: Upvote/downvote ideas (except your own)
- **Collaboration**: Express interest in collaborating on ideas
- **Sorting & Filtering**: Sort ideas by vote count or creation date

## Technologies

- **Backend**:
    - Java 17
    - Spring Boot 3.4.4
    - Spring Security
    - JWT Authentication
    - MySQL Database
- **Frontend**: (To be implemented)
- **DevOps**:
    - Docker
    - Docker Compose

## Prerequisites

- Java 17 JDK
- Maven 3.9.9
- Docker 20.10+
- Docker Compose 2.0+
- MySQL 8.0

## API Documentation

### Authentication

- `POST /auth/signup` – Register new user
- `POST /auth/login` – Login and get JWT token
- `POST /auth/logout` – Logout and invalidate token

### Ideas

- `GET /ideas/` – Get all ideas (supports sorting with `sortBy` and `sortOrder` params)
- `POST /ideas/creates` – Create new idea
- `POST /ideas/{id}/vote` – Vote on idea (`upvote=true/false`)
- `POST /ideas/{id}/collaborate` – Express interest in collaborating
- `GET /ideas/{id}/collaborators` – Get list of collaborators



## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | Database connection URL | jdbc:mysql://mysql:3306/CollabDB |
| SPRING_DATASOURCE_USERNAME | Database username | root |
| SPRING_DATASOURCE_PASSWORD | Database password | root |
| APP_JWT_SECRET | JWT secret key | (auto-generated) |
| APP_JWT_EXPIRATION | JWT expiration in ms | 86400000 (24h) |

## Deployment

### Production Considerations

- Set proper JWT secret in production
- Configure HTTPS
- Set proper database credentials
- Configure proper logging

## Future Enhancements

- Add email notifications
- Implement idea categories
- Add comment functionality
- Implement admin dashboard

## License

This project is licensed under the MIT License - see the LICENSE file for details.

