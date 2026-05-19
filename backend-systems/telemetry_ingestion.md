# Backend Systems: Telemetry & Log Ingestion Pipelines

This document details the scaling strategies, commit log buffering, stream processing architectures, and analytical database selections for ingesting millions of telemetry events per second.

---

## 1. High-Volume Ingestion Challenge

Modern enterprise applications continuously emit telemetry events (app diagnostics, page views, clickstream records, server CPU logs).
* **Write Bottlenecks**: Relational databases (e.g. Postgres, MySQL) use B-Trees for indexing. High-frequency random writes ($>10,000\text{ writes/sec}$) trigger frequent lock waits and index updates, depleting CPU threads.
* **Network Throttling**: Clients must not block on analytical logging. If the telemetry service goes offline or runs slowly, client logging must fail silently without affecting core client features.

---

## 2. Ingestion Pipeline Architecture

To absorb peak telemetry writes without system degradation, we decoupling ingestion from processing:

```
                              [Client Devices]
                                     |
                                     | Batch HTTP POST (JSON/Protobuf)
                                     v
                       +-------------+-------------+
                       |   Ingestion Gate API      |  --> Lightweight, stateless reverse proxy
                       +-------------+-------------+
                                     |
                                     v (Append-only write)
                       +-------------+-------------+
                       | Distributed Commit Log    |  --> Buffer (Apache Kafka / AWS Kinesis)
                       +-------------+-------------+
                                     |
                                     v (Stream Consumer pull)
                       +-------------+-------------+
                       | Stream Processing Engine  |  --> Flink / Spark Streaming
                       +-------------+-------------+
                                     |
                                     v (Bulk Batch Insert)
                       +-------------+-------------+
                       |  Analytical Column-Store  |  --> ClickHouse / InfluxDB
                       +---------------------------+
```

### 1. Ingestion Gate API
* Stateless servers accept requests containing arrays of aggregated client logs.
* Instead of validating schema or parsing payloads deep, they validate authorization tokens, check request structure, append the raw payload block to a partition inside a commit log, and instantly return an `HTTP 202` response.

### 2. Distributed Commit Log Buffer (e.g., Apache Kafka)
* Kafka acts as a high-throughput queue. Logs are appended sequentially to disk files (O(1) sequential I/O).
* Logs are partitioned using shard keys (e.g. client ID or session ID) to allow horizontal scaling across multiple broker machines.

### 3. Stream Processing Engine (e.g., Apache Flink)
* Dedicated consumer instances pull data streams from the commit log.
* Workers perform calculations (data cleansing, field transformations, mapping geolocation IP bounds).
* **Sliding Window Aggregation**: Flink pools metrics within specific time frames (e.g., aggregating error log frequencies every 10 seconds) to avoid performing single database insert calls per event.

### 4. Column-Oriented Storage (e.g., ClickHouse)
* Unlike standard row-oriented databases, ClickHouse stores column data contiguously on disk.
* **Why Column-Store**: Analytical queries generally compute aggregates over columns (e.g. `SELECT avg(response_time) FROM logs WHERE user_id = 12`). Reading contiguously stored columns reduces disk page reads.
