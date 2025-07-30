# ─── Stage 1: Build the application ───────────────────────
FROM gradle:8.12.0-jdk21 AS build
WORKDIR /app

# Copy and cache Gradle metadata
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# Copy source & build
COPY src ./src
RUN gradle clean bootJar --no-daemon

# ─── Stage 2: Package & Runtime ──────────────────────────
FROM openjdk:21-jdk-slim

# Install only the Debian libs Playwright’s Chromium actually needs
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      libglib2.0-0 \
      libnspr4 \
      libnss3 \
      libdbus-1-3 \
      libatk1.0-0 \
      libatk-bridge2.0-0 \
      libexpat1 \
      libatspi2.0-0 \
      libx11-6 \
      libx11-xcb1 \
      libxcursor1 \
      libxcomposite1 \
      libxdamage1 \
      libxext6 \
      libxfixes3 \
      libxrandr2 \
      libgbm1 \
      libxcb1 \
      libxkbcommon0 \
      libasound2 \
      libcups2 \
      libcairo2 \
      libcairo-gobject2 \
      libpango-1.0-0 \
      libpangocairo-1.0-0 \
      libgdk-pixbuf2.0-0 \
      libgtk-3-0 \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
