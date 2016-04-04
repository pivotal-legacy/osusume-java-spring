ci: refreshdb tests

refreshdb:
	@dropdb -e osusume-test
	@createdb -e osusume-test
	@psql -d osusume-test -f ./sql/initial_schema.ddl

tests:
	@./gradlew clean test build
