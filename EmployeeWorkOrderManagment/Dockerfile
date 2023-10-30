# Stage 1: Frontend Build
FROM node:latest as frontend
WORKDIR /src/main/frontend
COPY src/main/frontend/package*.json /src/main/frontend/
RUN npm install
COPY . .
WORKDIR src/main/frontend
RUN npm run build
COPY . /app

# Stage 2: Backend Build
FROM maven:latest as backend
WORKDIR .
COPY --from=frontend /app .
RUN mvn clean package


# Stage 3: Final Image
FROM eclipse-temurin:17
WORKDIR /app
EXPOSE 8080
COPY --from=backend /target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]