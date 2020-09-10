# Duo Universal Prompt Java Client

This library allows a web developer to quickly add Duo's interactive, self-service, two-factor authentication to any Java web login form.

See our developer documentation at http://www.duosecurity.com/docs/duoweb for guidance on integrating Duo 2FA into your web application.

What's here:
* `duo-universal-sdk` - The Duo SDK for interacting with the Duo Universal Prompt
* `duo-example` - An example web application with Duo integrated

# Usage
This library requires Java 8 or later, and uses Maven to build the JAR files.

Run `mvn package` to generate a JAR with dependencies, suitable for inclusion in a web application.

# Demo

## Build

From the root directory run:

`mvn clean install`

## Run

In order to run this project, ensure the values in `application.properties` are filled out with the values
from the Duo Admin Panel (clientId, clientSecret, api.host, and redirect.uri)

From the root of the `duo-example` project run the following to start the server:
`mvn spring-boot:run`

Navigate to <http://localhost:8080> to see a mock user login form.  Enter a Duo username and any password to initiate Duo 2FA.

# Testing

From the root directory run:

`mvn test`

# Linting

From the root directory run:

`mvn checkstyle:check`

# Support

Please report any bugs, feature requests, or issues to us directly at support@duosecurity.com.

Thank you for using Duo!

http://www.duosecurity.com/
