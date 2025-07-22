# Virtual Thread HTTP Server

## Requirements
- **Java Version**: 21

## Performance Statistics

### With Thread Pool
- **Active Java Threads**: 6  
- **Total Threads**: 11  
- **Heap Used**: 7.96 MB  

### With Virtual Threads
- **Active Java Threads**: 4 
- **Total Threads**: 9  
- **Heap Used**: 7.96 MB

# Java HTTP Server: Virtual Threads vs Traditional Thread Pool

This project demonstrates the performance difference between **Java 21 Virtual Threads** and a **pre-Java 21 Fixed Thread Pool** for handling concurrent HTTP requests. We use a simple `/ping` endpoint and load test it using Apache Bench (`ab`).

---

## 📋 Test Setup

- Endpoint: `/ping`
- Payload: `"pong"` (4 bytes)
- Requests: 100,000
- Concurrency: 10
- Tool: [Apache Bench](https://httpd.apache.org/docs/2.4/programs/ab.html)

---

## 🚀 Server Implementations

### 1. Virtual Thread Server (Java 21)

```java
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

int poolSize = Runtime.getRuntime().availableProcessors() * 2;
ExecutorService executor = Executors.newFixedThreadPool(poolSize);

###Comparisons
| Metric                      | **Port 8082** (Faster) | **Port 8084** (Slower) |
| --------------------------- | ---------------------- | ---------------------- |
| **Total Time Taken**        | 2183.17 sec            | 6471.08 sec            |
| **Requests per Second**     | 458.05 req/sec         | 154.53 req/sec         |
| **Mean Time per Request**   | 109.16 ms              | 323.55 ms              |
| **Transfer Rate**           | 43.84 KB/sec           | 14.79 KB/sec           |
| **95th Percentile Latency** | 115 ms                 | 349 ms                 |
| **Longest Request Time**    | 183 ms                 | 680 ms                 |


Increase concurrency levels (e.g., -c 100, -c 500)

Log JVM memory and thread usage

Output stats to CSV and plot results