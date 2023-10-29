FROM node:latest
WORKDIR .
COPY src/main/frontend/package*.json ./src/main/frontend/
WORKDIR ./src/main/frontend
RUN npm install
COPY . .
WORKDIR ./src/main/frontend
RUN npm run build

FROM maven:latest
WORKDIR .
COPY . .
RUN mvn clean package

FROM eclipse-temurin:17
VOLUME /tmp
EXPOSE 8080
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]