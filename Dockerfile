FROM maven:3.5.0-jdk-8-alpine

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN mvn install
RUN mv target/document-archive.jar .
RUN rm -r src/ target/ pom.xml README.md LICENSE

CMD java -jar document-archive.jar