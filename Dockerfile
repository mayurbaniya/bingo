FROM openjdk:17
EXPOSE 9090
ADD target/bingo-0.0.1-SNAPSHOT.jar bingo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java" , "-jar" , "/bingo-0.0.1-SNAPSHOT.jar"]