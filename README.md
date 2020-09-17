# sonia-webapp-qr-travel

## Initial Build
- `git clone https://github.com/thorsten-l/sonia-webapp-qr-travel.git`
- `cd sonia-webapp-qr-travel`
- `./00_CREATE_INITIAL_BUILD_ENVIRONMENT.sh`

## Setup

The `config` and the `private` directory will be ignored by `git`.

### Configuration Directory
- `config/chipher.cfg` cipher key dealing with configuration passwords
- `config/config.json` application configuration

### Private Directory
- `private/static/image/logo.png` application logo
- `private/static/imprint.html` Imprint
- `private/static/privacypolicy.html` Privacy Policy

## Build and Run

### Java Development Kit

- Development, Test and Production with [BellSoft Liberica JDK 11 LTS](https://bell-sw.com/pages/downloads/#/java-11-lts)

### CLI

- [Apache Maven](http://maven.apache.org/)
- `mvn clean install` production build
- `mvn -Pdevel clen install` development build

### IDE
- [Apache NetBeans 12](http://netbeans.apache.org/)