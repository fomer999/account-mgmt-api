Note: This is a Java Springboot application that uses its embedded tomcat as the web server.
Dlvr uses Rancher/CircleCi/DockerHub container and build management setup to deploy this application.
A developer can run this application locally using the commands mentioned below.
A datastore (CosmosDB) and SSL (Java Keystore)
needs to be setup for Lionfish before this application can be used.


# lf-cdn-api
This is a Java Springboot aaplication that exposes REST API endpoints. We have endpoints for:
(1) Cache Purge
(2) Customer Account Management
(3) Customer Configuration Management
(4) User Management

API SECURITY
The authentication/authorization of API endpoints is handled using OAuth.
Each API endpoint requires a security-context to be passed in via the "X-Security-Context" header.
The value for this header is obtained by hitting the in-house OAuth server (see code here git@40.127.136.217:lf-oauth-server.git)
Endpoint Authorization is defined by Roles assigned to the logged-in user. These Role assignments are stored as part of the User entity.

PROJECT CONFIG PROPERTIES
Config properties are defined in [`application.properties`](src/main/resources) for local development

HOW TO BUILD
This is a gradle project. For local builds, use this command:
$ ./gradlew clean build

HOW TO RUN
The "build" command will package everything into a jar file and place the jar under /build/libs.
$ To start the application locally, use this command:
java -Dspring.profiles.active=<env> -jar build/libs/<name-of-built-jar>
* <env> can be lab/staging/prod and tells the server which application.properties file to use
(For lab, application-lab.properties will be used)


API DOCUMENTATION
To access Swagger documentation, run the application and go to the following endpoints:
* For the UI: `http://localhost:8080/swagger-ui.html`
* For the JSON block for api-docs: `http://localhost:8080/v2/api-docs`