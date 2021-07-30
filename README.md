# Benchmarks for Sequence Builder

A few JMH benchmarks to compare the Sequence Builder implementation vs a plain iterator implementation.

## Run

Make sure:
- you have a JDK installed.
- you have maven installed.

1. Build the benchmarks jar: `mvn clean package`.
2. Execute the benchmarks: `java -jar benchmarks/target/benchmarks.jar`.