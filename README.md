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
| Metric                     | Virtual Threads (Port 8082) | Fixed Thread Pool (Port 8084) |
| -------------------------- | --------------------------- | ----------------------------- |
| ✅ Total Requests           | 100,000                     | 100,000                       |
| 🔄 Concurrency             | 10                          | 10                            |
| 🚫 Failed Requests         | 0                           | 0                             |
| ⏱ Time Taken               | **3.576 seconds**           | **4.657 seconds**             |
| ⚡ Requests/sec             | **27,963.13** 🔼            | **21,474.07**                 |
| 📉 Avg Time/Request        | 0.358 ms                    | 0.466 ms                      |
| 🧵 Max Request Time        | 79 ms                       | 125 ms                        |
| 🚚 Transfer Rate           | 2676 KB/sec                 | 2055 KB/sec                   |
| 🕒 95th Percentile Latency | 1 ms                        | 1 ms                          |
| 🕒 99th Percentile Latency | 1 ms                        | 2 ms                          |

📈 Observations
✅ Virtual Threads processed ~6,500 more requests/sec than the thread pool.

✅ Lower latency and higher throughput using Java 21 virtual threads.

✅ No failed requests in either implementation under the given load.

✅ Virtual threads scale better under blocking or I/O-heavy workloads.

📦 How to Run Benchmark
Virtual Thread Server
bash
Copy
Edit
java VirtualThreadServer
Fixed Thread Pool Server
bash
Copy
Edit
java ThreadPoolServer
Apache Bench Load Test
bash
Copy
Edit
ab -n 100000 -c 10 http://127.0.0.1:8082/ping
ab -n 100000 -c 10 http://127.0.0.1:8084/ping
🔬 Future Enhancements
Simulate blocking tasks (e.g., Thread.sleep(10))

Increase concurrency levels (e.g., -c 100, -c 500)

Log JVM memory and thread usage

Output stats to CSV and plot results