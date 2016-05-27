ci: refreshdb tests

refreshdb:
	@dropdb -e osusume-test
	@createdb -e osusume-test
	@psql -d osusume-test -f ./sql/initial_schema.ddl

loadsampledata:
	@psql -q -d osusume-test -f ./sql/SampleData.sql

alltests:
	@./gradlew clean test build

tests: alltests loadsampledata

start:
	@java -jar applications/api/build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar

deploy:
	@cf push osusume -p applications/api/build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar
