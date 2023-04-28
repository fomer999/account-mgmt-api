clean:
	./gradlew clean --stacktrace

build:
	./gradlew build test --stacktrace

test:
	./gradlew test --stacktrace

deb:
	./gradlew jar debian --stacktrace

ctest:
	cd cucumber && bash --login test.sh; cd ..

run:
	./gradlew bootRun --stacktrace
