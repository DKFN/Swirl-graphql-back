# This Dockerfile has two required ARGs to determine which base image
# to use for the JDK and which sbt version to install.

ARG OPENJDK_TAG=8u181
FROM openjdk:8

ARG SBT_VERSION=0.13.17

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt unzip && \
  sbt sbtVersion

RUN sbt clean

COPY ./ $HOME/src/
WORKDIR $HOME/src
RUN ls
RUN sbt dist
RUN unzip /src/target/universal/swirl-graphql-backend-0.1.zip -d /src/server
RUN ls /src/server
CMD ["/src/server/swirl-graphql-backend-0.1/bin/swirl-graphql-backend", "-Dplay.http.secret.key=SwirlIsMagic", "-Dhttp.port=8080"]

