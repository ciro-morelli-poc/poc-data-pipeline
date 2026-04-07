![diagram](./architecture.svg)

docker build -t poc-pipeline .
docker run --rm   -v $(pwd)/input.csv:/poc-data-pipeline/input.csv   -v $(pwd)/output.csv:/poc-data-pipeline/output.csv  poc-pipeline


Fuori Scope:
-Retry con exponential Backoff
-Circuit breaker
-Idempotenza messaggi