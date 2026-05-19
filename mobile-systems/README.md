# Mobile Systems & Client Architectures

This module covers client-side system architecture, UI layout rendering, hardware-backed memory management, push notification networks, and offline data consistency.

---

## 📂 Module Directory Index

* **[Offline-First Synchronization](./offline_sync.md)**: SQLite Single Source of Truth, delta changesets, optimistic UI updates, outbox queues, and Jitter-backed backoff retries.
* **[UI Rendering Pipelines](./rendering_pipeline.md)**: Flutter's three-tree architecture vs. Android measure/layout draw loops and iOS CoreAnimation commit cycles.
* **[List Pagination Systems](./pagination.md)**: Viewport-aware scroll prefetching thresholds, cell recycling, and Offset vs. Cursor-based pagination.
* **[Image Caching & Memory math](./image_caching.md)**: Decoded bitmap memory arithmetic, RAM/Disk cache tiers (L1 & L2), downsampling, and GC pressure mitigation.
* **[Real-Time Video Streaming](./video_streaming.md)**: WebRTC P2P streaming, SDP session negotiations, STUN/TURN NAT traversal, and hardware rendering context.
* **[Push Notification Delivery](./notification_delivery_pipeline.md)**: Silent data push executions, OS notification service extensions, and secure hardware-key decrypt loops.
* **[Real-Time Chat Client](./chat_client_design.md)**: Duplex WebSockets vs. gRPC bidirectional streams, cursor reconciliation, and optimistic outbox queue states.
* **[Background Execution Constraints](./background_execution_constraints.md)**: OS background execution constraints (battery, charger connectivity, network unmetered limits) mapped to Android WorkManager and iOS BackgroundTasks.
