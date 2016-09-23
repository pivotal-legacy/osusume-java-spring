FROM openjdk:8

# Install dependencies
RUN apt-get update && apt-get install -y \
    make \
    postgresql \
    postgresql-client \

    && \

    apt-get clean \

    && \

    rm -Rf /var/lib/apt/lists/*

# Override postgres connection settings to allow local TCP
ENV POSTGRESQL_VERSION 9.4

COPY ./docker/pg_hba.conf /etc/postgresql/$POSTGRESQL_VERSION/main/pg_hba.conf
RUN chown postgres:postgres /etc/postgresql/$POSTGRESQL_VERSION/main/pg_hba.conf

# Copy app assets
RUN mkdir /usr/local/osusume-java-spring
COPY . /usr/local/osusume-java-spring
RUN chown -R postgres:postgres /usr/local/osusume-java-spring

WORKDIR /usr/local/osusume-java-spring
USER postgres

ENV OSUSUME_DATABASE_URL jdbc:postgresql://localhost/osusume-dev
ENV OSUSUME_DATABASE_USER postgres

# Populate DB
RUN /etc/init.d/postgresql start && dropdb --if-exists -e osusume-dev && createdb -e osusume-dev -O postgres
RUN /etc/init.d/postgresql start && make migrate
RUN /etc/init.d/postgresql start && make loadsampledata

RUN /etc/init.d/postgresql start && dropdb --if-exists -e osusume-test && createdb -e osusume-test -O postgres
RUN /etc/init.d/postgresql start && make test-migrate
RUN /etc/init.d/postgresql start && make test-loadsampledata

# Run tests and build
RUN OSUSUME_DATABASE_URL=jdbc:postgresql://localhost/osusume-test /etc/init.d/postgresql start && ./gradlew clean test build

# Start app
CMD /etc/init.d/postgresql start && make start
