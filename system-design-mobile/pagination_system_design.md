# Mobile System Design: High-Performance List Pagination

This system design document details client-side viewport optimizations, list virtualizations, and network pagination mechanisms for rendering massive feeds.

---

## 1. Viewport Rendering & List Virtualization

Mobile viewports render a limited number of elements (often 3–6 items) visible on screen at once. To render infinite lists smoothly:
* **The Problem**: Allocating thousands of UI Widget classes consumes immense heap space and triggers GC lags, freezing the UI.
* **The Solution (Virtualization)**: ListView controllers recycle visible cells. When a cell scrolls off the top of the viewport, its container widget is kept in memory, but its data bindings are overwritten with the next cell appearing at the bottom, maintaining a constant visual node footprint.

---

## 2. Offset-Based vs. Cursor-Based Pagination

To load data incrementally:

| Strategy | Server Query | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Offset-Based** | `LIMIT 20 OFFSET 40` | Simple backend mapping, supports direct page jumps. | **Duplicates/Skips**: If new items are posted while scrolling, items shift offsets, causing duplicate or skipped renders on the client. Unscalable for large databases due to slow SQLite/server sequential scans. |
| **Cursor-Based** | `LIMIT 20 WHERE id < cursor` | Guaranteed consistency. Excellent performance since SQL queries utilize indices. | No direct page jumps (client must traverse sequentially). |

```mermaid
graph TD
    subgraph PaginationComparison ["Offset Shift Duplicate Trap"]
        ClientFeed1["Client View (Offset 0-1): [Item A, Item B]"]
        NewItem["New Item C Posted!"] --> DBChanges["DB: [Item C, Item A, Item B]"]
        
        ClientFeed1 -->|Scrolled: Offset 2| ClientFeed2["Client View (Offset 2-3): [Item B, Item D]"]
        Note over ClientFeed2: Item B is rendered AGAIN because it shifted index!
    end
```

---

## 3. Prefetching & Viewport Boundary Triggers

Rather than waiting for the user to hit the absolute bottom of the list (which shows a blank screen or a loading indicator):
* **Dynamic Prefetching Bounds**: We define a prefetch threshold: `prefetchThreshold = 4`.
* **The Trigger**: The scroll controller monitors scroll indexes. If `currentIndex >= totalItems - prefetchThreshold` and no active network job is running, the app preemptively dispatches the next cursor query in a background thread, ensuring a seamless, infinite scroll experience.
