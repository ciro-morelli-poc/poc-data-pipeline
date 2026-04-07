# ADR – Choice of Runtime and Architecture for the Rate Limiting POC

## 1. Context
The goal of this Proof of Concept is to evaluate different **rate limiting** strategies in a concurrent system, with a focus on:

- queue and concurrency management  
- implementation simplicity  
- observability and throughput control  
- potential extensibility toward a distributed architecture  

There were no strict constraints regarding the runtime or programming language. The choice was primarily driven by development speed and the ability to model rate limiting logic clearly.

## 2. Decision
The selected runtime for the POC is **Java (JDK 21) using the `java.util.concurrent` library**.


## 3. Rationale
### Why Java?
- **Mature concurrency model**: the `concurrent` package provides executors, thread pools, blocking queues, semaphores, and rate limiters out of the box.  
- **Explicit control**: it allows the rate limiting logic to be modeled transparently, without hiding it behind serverless abstractions.  
- **Fast to implement**: for a POC focused on logic rather than infrastructure, Java minimizes overhead.  
- **Debuggability and observability**: thread dumps, JVM metrics, and profiling tools make it easy to analyze system behavior under load.

## 4. Alternatives Considered

### **A. Python + Spark / PySpark**
**Pros:**  
- Strong fit for distributed and high-throughput batch or streaming workloads  
- Rich ecosystem
- Spark provides built‑in rate limiting for streaming pipelines  

**Cons:**  
- Overkill for a local POC  
- Python’s GIL limits CPU-bound concurrency in pure Python  
- Spark introduces infrastructure complexity (cluster, sessions, orchestration)

**When it would be preferable:**  
- If rate limiting were part of a distributed ETL or streaming pipeline  
- If testing backpressure on high-volume data streams

### **B. Serverless Architecture (AWS Lambda + SQS + API Gateway + DynamoDB)**
**Pros:**  
- Rate limiting available *out of the box* (API Gateway throttling, SQS throughput, Lambda concurrency)  
- Automatic scalability  
- No thread or infrastructure management  
- Ideal for bursty workloads  

**Cons:**  
- Rate limiting logic becomes implicit and tied to AWS services  
- More complex debugging  
- Variable latency and cloud costs  
- Requires infrastructure setup (IAM, queues, functions, permissions)

**When it would be preferable:**  
- In a production-grade cloud-native system  
- When resilience and scalability matter more than explicit logic modeling

### **C. Node.js with Event Loop and Worker Threads**
**Pros:**  
- Event-driven model well suited for I/O-bound workloads  
- Mature rate limiting libraries (Bottleneck, rate-limiter-flexible)  
- Easy to containerize  

**Cons:**  
- Less suitable for CPU-bound workloads  
- Worker Threads are less intuitive than Java’s concurrency primitives  
- Does not provide specific advantages for this POC  

**When it would be preferable:**  
- If the final system were an API gateway or microservice built in JavaScript/TypeScript

## 5. Consequences
### Positive
- The POC was implemented quickly  
- The rate limiting logic is explicit and easy to extend  
- System behavior is observable and reproducible  
- The solution can be ported to a real Java microservice  

### Negative
- Not representative of a cloud-native or serverless architecture  
- Does not test distributed or multi-node scenarios  
- Does not leverage built-in rate limiting mechanisms (API Gateway, Spark Streaming)

## 6. Final Decision
For a POC focused on **rate limiting logic**, Java with `concurrent` offered the best balance of:

- simplicity  
- control  
- development speed  
- clarity of the concurrency model  

Serverless or distributed alternatives remain valid candidates for a future production evolution.