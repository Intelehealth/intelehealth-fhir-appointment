FROM maven:3.6-openjdk-8

WORKDIR /app
COPY . .

RUN mvn clean package -Dmaven.test.skip=true

EXPOSE 5004

#CMD ["java","-jar","/app/target/appointments.exchange-0.0.1-SNAPSHOT.jar"]