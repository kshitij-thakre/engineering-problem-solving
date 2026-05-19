# Mobile Image Caching & Memory Management

This document details image loading lifecycles, decoded memory equations, garbage collection protection, and two-level (RAM + Disk) cache architectures.

---

## 1. The Bitmap Memory Threat

In mobile applications, image assets (bitmaps) are the single largest source of Heap memory usage and Out-Of-Memory (OOM) crashes:
* **The Compressed Size**: An image file stored on a server or disk (e.g. JPG, PNG) might be compressed down to **$100\text{KB}$**.
* **The Decoded Size**: To render the image on screen, the mobile operating system must decode it into raw pixel arrays in memory. The decoded memory footprint is calculated as:
  $$\text{Decoded Memory Size} = \text{Width (pixels)} \times \text{Height (pixels)} \times \text{Bytes Per Pixel}$$
  * For a standard `ARGB_8888` pixel configuration, every pixel requires **$4\text{ bytes}$**.
  * A $2000 \times 2000$ pixel image requires:
    $$2000 \times 2000 \times 4 = 16,000,000\text{ bytes} \approx 15.25\text{MB of Heap RAM}$$
  * If a user scrolls quickly through an infinite list displaying 50 such raw images, the app will instantly consume **$760\text{MB}$** of heap space, triggering an OOM crash.

---

## 2. Two-Level Caching System (RAM + Disk)

To keep images instantly available while protecting device memory constraints, image loading frameworks (e.g. Coil, Glide, Kingfisher, Flutter Cache Manager) run a **Two-Level Cache Hierarchy**:

```
                              [Image Request]
                                     |
                                     v
                       +-------------+-------------+
                       |   Memory Cache (RAM L1)   |  --> Hit: Return Bitmap (Sub-ms)
                       +-------------+-------------+
                                     | Miss
                                     v
                       +-------------+-------------+
                       |    Disk Cache (Disk L2)   |  --> Hit: Decode file, write to L1, return
                       +-------------+-------------+
                                     | Miss
                                     v
                       +-------------+-------------+
                       |       Network Call        |  --> Download, write to Disk L2,
                       +---------------------------+      decode, write to L1, return
```

### 1. Memory Cache (L1 Cache - RAM)
* **Goal**: Provide sub-millisecond retrieval speeds for recently rendered images.
* **Mechanism**: Bounded [LRU Cache](../system-design/lru_cache.md) backed by a HashMap.
* **Memory Limits**: Typically capped at a percentage of available device heap (e.g. 15% to 20% of maximum application memory).

### 2. Disk Cache (L2 Cache - Persistent Storage)
* **Goal**: Cache downloaded image files across application restarts to bypass cellular network calls.
* **Mechanism**: Bounded Disk LRU Cache storing raw encoded bytes.
* **Limits**: Typically capped at a static limit (e.g., 250MB to 500MB) on local device flash storage.

---

## 3. Advanced Memory Management Strategies

To protect the main rendering thread and minimize heap pressure:

* **Downsampling**: Decoders resize the image *during* decoding to match the exact dimensions of the target UI container rather than decoding the full high-resolution asset (e.g., decoding a $2000 \times 2000$ image down to $200 \times 200$ saves 99% of decoded heap RAM).
* **Bitmap Pooling**: Allocating byte arrays continually triggers GC churn. Runtimes maintain a pool of reusable byte arrays (Bitmap Pool). When an image goes off-screen, its backing array is reset and put in the pool to be reused for the next incoming image, bypassing GC allocations entirely.
* **Low Memory Triggers**: Mobile systems listen to OS events (e.g., `onTrimMemory` on Android, `didReceiveMemoryWarning` on iOS). When physical RAM runs low, the L1 memory cache is cleared immediately to prevent background termination.
