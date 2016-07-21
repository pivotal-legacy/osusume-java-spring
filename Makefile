ci: tests

refreshdb: test-refreshdb dev-refreshdb

test-refreshdb:
	@dropdb -e osusume-test
	@createdb -e osusume-test

dev-refreshdb:
	@dropdb -e osusume-dev
	@createdb -e osusume-dev

migrate:
	@./gradlew flywayMigrate

test-migrate:
	@OSUSUME_DATABASE_URL=jdbc:postgresql://localhost/osusume-test ./gradlew flywayMigrate

loadsampledata:
	@psql -q -d osusume-dev -f ./sql/SampleData.sql

test-loadsampledata:
	@psql -q -d osusume-test -f ./sql/SampleData.sql

tests:
	@./gradlew clean test build

start:
	@java -jar build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar

deploy: tests justdeploy

justdeploy:
	@cf push osusume -p build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar
