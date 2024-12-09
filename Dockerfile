FROM openjdk:21
EXPOSE 8081
ADD target/smart_contact_manager.jar smart_contact_manager.jar
ENTRYPOINT ["java","-jar","/smart_contact_manager.jar"]