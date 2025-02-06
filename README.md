# **Auto Finder Bot**

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Java](https://img.shields.io/badge/java-21-brightgreen) ![Gradle](https://img.shields.io/badge/build-gradle-green)

## 🚀 **About the Project**

THis project is a telegram bot for helping users to find cars on otomoto:
> The project is based on Spring Boot with PostgreSQL database.
> Users are able to set up filter for themselves so that new posted cars on otomoto will be sent to them via telegram

---

## 📋 **Prerequisites**

Before you begin, ensure you have the following installed:

- **Java 21**: [Download here](https://www.oracle.com/java/technologies/javase-downloads.html)
- **Gradle**: [Installation Guide](https://gradle.org/install/)
- **Docker** [Download here](https://www.docker.com/) (for convenient development)
- **IDE**: [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows) (or your preferred IDE)

---

## 🔧 **Setup and Installation**

Follow these steps to set up the project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/OjAntoni/AutoFinderBot.git

2. Go to content root:
   ```bash
   cd AutoFinderBot

3. Register your bot and obtain bot token from [Bot father](https://telegram.me/BotFather)
   
4. Create `.env` file containig telegram token:
   ```bash
   echo "TELEGRAM_BOT_TOKEN={Your token here}" > .env

5. Run docker compose to start application in development mode:
   ```bash
   docker-compose -p dev up -d
   ```
   For production deployment use:
   ```bash
   docker-compose -p prod -f docker-compose.yml -f docker-compose.prod.yml up -d

### 💡 **Tips for Docker compose**
If you added some changes to your code it's more convenient and simple to run `docker-compose -p dev up -d --build` that will rebuild the application.
If you would like to store dev results for some reason simply add such lines to the `docker-compose.override.yml` file:
```
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```
If you want to run database container separately and for example run application via IDE use:
`docker run --name postgres-otomoto -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v otomoto-data:/var/lib/postgresql/data -p 5432:5432 -d postgres`
In such case of a standalone application start up remember to set up environment variable `TELEGRAM_BOT_TOKEN` with your token.
