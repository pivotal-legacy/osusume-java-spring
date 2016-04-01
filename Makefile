ci: refreshdb tests

refreshdb:
	@dropdb osusume-test
	@createdb osusume-test
	@psql -d osusume-test -f ./sql/initial_schema.ddl

tests:
	@./gradlew clean test build
