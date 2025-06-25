# AutoFinderBot

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Java](https://img.shields.io/badge/java-21-brightgreen) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-green) ![Gradle](https://img.shields.io/badge/build-gradle-green) ![Version](https://img.shields.io/badge/version-1.0.2-blue)

## 🚀 About the Project

AutoFinderBot is a Telegram bot designed to help users find cars on Otomoto, a popular car marketplace. The bot allows users to set up personalized filters and automatically notifies them when new listings matching their criteria are posted.

### Key Features

- **Personalized Car Filters**: Set up custom filters based on make, model, price range, year, and more
- **Real-time Notifications**: Receive instant Telegram notifications when new matching cars are listed
- **User-friendly Interface**: Easy-to-use Telegram bot commands and interactions
- **Secure Authentication**: JWT-based authentication for the web API
- **RESTful API**: Access and manage your filters and notifications programmatically

### Technology Stack

- **Backend**: Java 21, Spring Boot 3.4.2
- **Database**: PostgreSQL with Liquibase migrations
- **API Documentation**: OpenAPI (Springdoc)
- **Testing**: JUnit 5, TestContainers, Rest Assured
- **Build Tool**: Gradle 8.x
- **Containerization**: Docker and Docker Compose
- **CI/CD**: JaCoCo for test coverage verification

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21**: [Download here](https://www.oracle.com/java/technologies/javase-downloads.html)
- **Gradle 8.x+**: [Installation Guide](https://gradle.org/install/) (or use the included Gradle wrapper)
- **Docker and Docker Compose**: [Download here](https://www.docker.com/) (for development and deployment)
- **IDE**: [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows) (recommended) or your preferred IDE
- **Telegram Account**: Required to create a bot via [BotFather](https://telegram.me/BotFather)

---

## 🔧 Setup and Installation

Follow these steps to set up the project locally:

### 1. Clone the Repository

```bash
git clone https://github.com/OjAntoni/AutoFinderBot.git
cd AutoFinderBot
```

### 2. Configure Environment Variables

Create environment files for development and production:

#### Obtaining a Telegram Bot Token

To obtain a Telegram bot token:

1. Open Telegram and search for [@BotFather](https://telegram.me/BotFather)
2. Start a chat with BotFather and send the command `/newbot`
3. Follow the instructions to create a new bot
4. Once created, BotFather will provide you with a token that looks like `123456789:ABCDefGhIJKlmNoPQRsTUVwxyZ`
5. Copy this token for use in your environment configuration

#### For Development (`.env.dev`):

```bash
# Required
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
APP_JWT_SECRET=your_jwt_secret_key

# Optional - defaults will be used if not specified
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=autofinderbot
POSTGRES_PORT=5432
```

#### For Production (`.env`):

Similar to development but with stronger passwords and different configuration values as needed.

### 3. Build and Run

#### Using Docker Compose (Recommended)

For development:
```bash
docker-compose -p dev up -d
```

For production:
```bash
docker-compose -p prod -f docker-compose.yml -f docker-compose.prod.yml up -d
```

#### Manual Build and Run

Build the application:
```bash
./gradlew clean build
```

Run the application (requires PostgreSQL running):
```bash
java -jar build/libs/autofinderbot-1.0.2.jar
```

---

## 🧪 Testing

The project uses JUnit 5 with TestContainers for integration testing. Docker must be running to execute tests.

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific tests
./gradlew test --tests "com.example.autofinderbot.web.controller.*"
```

### Test Coverage

The project enforces strict test coverage requirements:
- 80% line coverage
- 50% branch coverage
- 100% method coverage

View the JaCoCo test coverage report at `build/reports/jacoco/test/html/index.html` after running tests.

---

## 📚 API Documentation

The API documentation is available via Swagger UI when the application is running:

```
http://localhost:8080/swagger-ui.html
```

---

## 💡 Docker Tips

### Rebuilding After Code Changes

```bash
docker-compose -p dev up -d --build
```

### Persisting Development Data

Add these lines to `docker-compose.override.yml`:

```yaml
services:
  postgres:
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

### Running Database Separately

```bash
docker run --name postgres-otomoto -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v otomoto-data:/var/lib/postgresql/data -p 5435:5432 -d postgres
```

When running the application standalone, set the `TELEGRAM_BOT_TOKEN` environment variable.

---

## 🤝 Contributing

We welcome contributions to AutoFinderBot! Please follow our contribution guidelines to ensure a smooth collaboration process.

### Contribution Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style Guidelines

#### Java Code Style

1. **Naming Conventions**
   - Classes: PascalCase (e.g., `CarService`)
   - Methods and variables: camelCase (e.g., `findCarById`)
   - Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
   - Packages: lowercase (e.g., `com.example.service`)

2. **Formatting**
   - Use 4 spaces for indentation, not tabs
   - Maximum line length: 120 characters
   - Use braces with all control structures, even single-line statements
   - Place opening braces on the same line as the declaration

3. **Documentation**
   - Add JavaDoc comments for all public classes and methods
   - Include `@param`, `@return`, and `@throws` tags where applicable
   - Document non-obvious implementation details with inline comments

4. **Best Practices**
   - Follow SOLID principles
   - Make classes final when they're not designed for extension
   - Use constructor injection for dependencies
   - Prefer immutable objects when possible
   - Use Optional for nullable return values instead of null

#### Testing Guidelines

1. **Naming Conventions**
   - Test classes: `[ClassUnderTest]Test` (e.g., `CarServiceTest`)
   - Test methods: Use descriptive names with the following suffixes:
     - `_PosTC` for positive test cases (e.g., `authenticateUser_PosTC`)
     - `_NegTC` for negative test cases (e.g., `invalidCredentials_NegTC`)

2. **Structure**
   - Follow the Arrange-Act-Assert (AAA) pattern
   - Use descriptive test method names that explain the test scenario
   - Group related tests in nested classes when appropriate

3. **Base Test Classes**
   - `BaseSpringBootTest`: Base class for all tests that need Spring context and database access
     - Sets up PostgreSQL TestContainer
     - Configures transaction management
     - Provides mock Telegram token and JWT secret
   - `BaseRestApiTest`: Extends `BaseSpringBootTest` for REST API testing
     - Configures RestAssured for API testing
     - Provides JWT token generation for authentication
   - `BaseTelegramListenerTest`: Extends `BaseSpringBootTest` for Telegram bot testing
     - Provides mocked Telegram Update objects
     - Configures the StrategyContext for command handling

4. **Test Utility Classes**
   - `TestRequestSender`: Utility for sending HTTP requests with different authentication contexts
     - `asAdmin()`: Send requests as admin user
     - `unauthorized()`: Send requests without authentication
     - `as(username, password)`: Send requests as specific user
   - `JwtTestHelper`: Utility for JWT authentication in tests
     - `loginAs(username, password)`: Get JWT token for user
     - `createAuthHeaders(username, password)`: Create HTTP headers with authentication

5. **Coverage Requirements**
   - All new code must meet the minimum coverage requirements
   - Write tests for both positive and negative scenarios
   - Use mocks appropriately to isolate the unit under test

#### Git Commit Guidelines

1. **Commit Message Format**
   ```
   <type>(<scope>): <subject>

   <body>

   <footer>
   ```

2. **Types**
   - feat: A new feature
   - fix: A bug fix
   - docs: Documentation changes
   - style: Code style changes (formatting, missing semicolons, etc.)
   - refactor: Code changes that neither fix a bug nor add a feature
   - test: Adding or modifying tests
   - chore: Changes to the build process or auxiliary tools

3. **Subject**
   - Use imperative, present tense: "add" not "added" or "adds"
   - Don't capitalize the first letter
   - No period at the end

4. **Body**
   - Use imperative, present tense
   - Include motivation for the change and contrast with previous behavior

5. **Footer**
   - Reference issues and pull requests
   - Breaking changes should start with "BREAKING CHANGE:"

### Pull Request Process

1. Ensure your code follows the style guidelines
2. Update documentation if necessary
3. Include tests for new functionality
4. Ensure all tests pass and coverage requirements are met
5. Get at least one code review from a maintainer

### Code Review Guidelines

1. **Reviewer Responsibilities**
   - Check for adherence to coding standards
   - Verify that tests are comprehensive and pass
   - Ensure the code is maintainable and follows best practices
   - Provide constructive feedback

2. **Author Responsibilities**
   - Address all review comments
   - Explain design decisions when requested
   - Be open to suggestions for improvement

### Development Environment Setup

1. **IDE Configuration**
   - Import the project code style settings
   - Enable automatic code formatting on save
   - Configure static code analysis tools

2. **Recommended Plugins**
   - SonarLint for code quality checks
   - Lombok plugin for annotation processing
   - JaCoCo plugin for test coverage visualization

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Contact

Project Maintainer - [GitHub Profile](https://github.com/OjAntoni)

Project Link: [https://github.com/OjAntoni/AutoFinderBot](https://github.com/OjAntoni/AutoFinderBot)
