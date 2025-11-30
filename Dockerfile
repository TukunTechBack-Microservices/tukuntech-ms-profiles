# Etapa 1: build con Maven y Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom y las fuentes
COPY pom.xml .
COPY src ./src

# Compilamos el jar (sin tests para que sea más rápido)
RUN mvn -q -DskipTests package

# Etapa 2: imagen ligera solo con el jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""

# Puerto Dev (local): 8083
EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8083}"]
