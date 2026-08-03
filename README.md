# Duo Universal Prompt Java Client

[![Build Status](https://github.com/duosecurity/duo_universal_java/workflows/Build%20and%20Test%20with%20Maven/badge.svg)](https://github.com/duosecurity/duo_universal_java/actions)
[![Issues](https://img.shields.io/github/issues/duosecurity/duo_universal_java)](https://github.com/duosecurity/duo_universal_java/issues)
[![Forks](https://img.shields.io/github/forks/duosecurity/duo_universal_java)](https://github.com/duosecurity/duo_universal_java/network/members)
[![Stars](https://img.shields.io/github/stars/duosecurity/duo_universal_java)](https://github.com/duosecurity/duo_universal_java/stargazers)
[![License](https://img.shields.io/badge/License-View%20License-orange)](https://github.com/duosecurity/duo_universal_java/blob/main/LICENSE)


This library allows a web developer to quickly add Duo's interactive, self-service, two-factor authentication to any Java web login form.

See our developer documentation at http://www.duosecurity.com/docs/duoweb for guidance on integrating Duo 2FA into your web application.

What's here:
* `duo-universal-sdk` - The Duo SDK for interacting with the Duo Universal Prompt
* `duo-example` - An example web application with Duo integrated

# Usage
This library requires Java 8 or later (tested through Java 16) and uses Maven to build the JAR files.

Run `mvn package` to generate a JAR with dependencies, suitable for inclusion in a web application.

The Duo Universal Client for Java is available from Duo Security on Maven.  Include the following in your dependency definitions:
```
<!-- https://central.sonatype.com/artifact/com.duosecurity/duo-universal-sdk -->
<dependency>
    <groupId>com.duosecurity</groupId>
    <artifactId>duo-universal-sdk</artifactId>
    <version>1.3.2</version>
</dependency>
```
See https://central.sonatype.com/artifact/com.duosecurity/duo-universal-sdk/1.3.2 for more details.

## TLS 1.2 and 1.3 Support

Duo_universal_java uses the Java cryptography libraries for TLS operations. Both TLS 1.2 and 1.3 are supported by Java 8 and later versions.

## Verifying releases

Artifacts published to Maven Central are signed with one of Duo's Maven signing keys.
The public keys are in [`KEYS`](KEYS) in this repository.

```
curl -O https://raw.githubusercontent.com/duosecurity/duo_universal_java/main/KEYS
gpg --import KEYS
gpg --verify duo-universal-sdk-1.3.2.jar.asc duo-universal-sdk-1.3.2.jar
```

The `.jar.asc` signature files are available alongside each artifact on Maven Central,
for example <https://repo1.maven.org/maven2/com/duosecurity/duo-universal-sdk/1.3.2/>.

| Versions | Key fingerprint |
| --- | --- |
| 1.3.2 and later | `7ED4 A780 3AFC 6DE8 47DF  9A3F 70EE 73F2 1701 2D0E` |
| 1.0.2 through 1.3.1 | `20FF 0D66 B2D0 202C 1544  7339 7E77 F31E 27A4 AEA2` (expired 2026-01-27) |

A `Good signature` result confirms the artifact was signed with a Duo key. GPG also
reports the key as untrusted unless you have signed it yourself, and reports the
retired key as expired; neither affects the validity of signatures made while that
key was valid.

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
