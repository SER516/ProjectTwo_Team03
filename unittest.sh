#/usr/bin/env bash

./make.sh

# All files ending in .*Tests.java will be compiled and executed as JUnit tests.

# List all test files        # Get the base names (E.G. ServerTests.class --> ServerTests)    # Execute the tests
ls bin/server/*Tests.class | xargs -I '{}' basename '{}' .class | xargs -I '{}' echo server.'{}' | xargs java -cp "$CLASSPATH:bin" org.junit.runner.JUnitCore
ls bin/client/*Tests.class | xargs -I '{}' basename '{}' .class | xargs -I '{}' echo client.'{}' | xargs java -cp "$CLASSPATH:bin" org.junit.runner.JUnitCore
