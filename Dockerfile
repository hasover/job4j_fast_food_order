
FROM openjdk:17.0.2-jdk
WORKDIR /app
COPY /target/fast_food_order-1.0-SNAPSHOT.jar app.jar
CMD java -jar app.jar