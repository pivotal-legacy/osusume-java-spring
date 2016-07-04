ci: refreshdb tests

refreshdb:
	@dropdb -e osusume-dev
	@createdb -e osusume-dev
	@psql -d osusume-dev -f ./sql/initial_schema.ddl
	@dropdb -e osusume-test
	@createdb -e osusume-test
	@psql -d osusume-test -f ./sql/initial_schema.ddl

loadsampledata: test-loadsampledata dev-loadsampledata

test-loadsampledata:
	@psql -q -d osusume-test -f ./sql/SampleData.sql

dev-loadsampledata:
	@psql -q -d osusume-dev -f ./sql/SampleData.sql

alltests:
	@./gradlew clean test build

tests: alltests test-loadsampledata

start:
	@java -jar build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar

deploy: alltests justdeploy

justdeploy:
	@cf push osusume -p build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar
