FROM maven:3.8.2-jdk-11-slim AS builder
RUN mkdir app
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:go-offline
COPY src ./src
RUN mvn -e -B package -DskipTests

FROM amazoncorretto:17-alpine AS release

RUN addgroup -S emailuser && adduser -S -s /bin/false emailuser -G emailuser
RUN mkdir app && chown -R emailuser:emailuser /app

COPY --from=builder /app/target/rewe-send-email-0.0.1-RELEASE.war /app/

WORKDIR /app

USER emailuser:emailuser
EXPOSE 7500

CMD ["java", "-jar", "send-email-0.0.1-RELEASE.war"]