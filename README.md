# Duo Universal Prompt Java Client

[![Build Status](https://github.com/duosecurity/duo_universal_java/workflows/Build%20and%20Test%20with%20Maven/badge.svg)](https://github.com/duosecurity/duo_universal_java/actions)
[![Issues](https://img.shields.io/github/issues/duosecurity/duo_universal_java)](https://github.com/duosecurity/duo_universal_java/issues)
[![Forks](https://img.shields.io/github/forks/duosecurity/duo_universal_java)](https://github.com/duosecurity/duo_universal_java/network/members)
[![Stars](https://img.shields.io/github/stars/duosecurity/duo_universal_java)](https://github.com/duosecurity/duo_universal_java/stargazers)
[![License](https://img.shields.io/badge/License-View%20License-orange)](https://github.com/duosecurity/duo_universal_java/blob/master/LICENSE)


This library allows a web developer to quickly add Duo's interactive, self-service, two-factor authentication to any Java web login form.

See our developer documentation at http://www.duosecurity.com/docs/duoweb-v4 for guidance on integrating Duo 2FA into your web application.

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
