# Cache Eviction Flow & Mechanics

This document provides visual flowcharts mapping out cache queries, hit transitions, and eviction processing inside Least Recently Used (LRU) and Least Frequently Used (LFU) mobile caching engines.

---

## 1. LRU Cache hit / Miss Flowchart

When an application queries the cache for a specific key (e.g. fetching a cached image bitmap), the LRU Cache performs the following checks:

```mermaid
graph TD
    Start([Application requests Key]) --> QueryMap{Is Key in HashMap?}
    
    QueryMap -- NO (Cache Miss) --> ReturnMiss[Return -1 / Trigger Network Fetch]
    
    QueryMap -- YES (Cache Hit) --> GetNode[Retrieve Node Reference]
    GetNode --> DetachNode[Detach Node from Doubly Linked List]
    DetachNode --> SpliceHead[Splice Node directly after Dummy Head sentinel]
    SpliceHead --> ReturnVal[Return Node value]
    
    ReturnVal --> End([Request Resolved])
    ReturnMiss --> End
```

---

## 2. LRU Cache Insertion and Eviction Mechanics

When writing new data (e.g. saving an API response payload), the cache evaluates memory limits:

```mermaid
graph TD
    Start([put key, value]) --> CheckKey{Is Key in HashMap?}
    
    CheckKey -- YES (Update) --> UpdateVal[Update existing Node value]
    UpdateVal --> MoveHead[Move Node to Head position]
    
    CheckKey -- NO (Insert New) --> CreateNode[Instantiate new Node]
    CreateNode --> WriteMap[Save Node to HashMap]
    WriteMap --> InsertHead[Insert Node at Head position]
    InsertHead --> CheckCap{Is Size > Capacity?}
    
    CheckCap -- NO --> End([Write Complete])
    CheckCap -- YES (Evict) --> FindTail[Locate Node directly preceding Dummy Tail]
    FindTail --> RemoveList[Remove Node from Doubly Linked List]
    RemoveList --> DeleteMap[Wipe key from HashMap]
    DeleteMap --> End
    MoveHead --> End
```

---

## 3. LFU Cache Dual-Map Dynamic Bucket Shifts

Unlike LRU which only tracks recency, LFU organizes keys into **frequency buckets**. Accessing a key moves it across lists:

```mermaid
sequenceDiagram
    participant App as Application Core
    participant Cache as cache HashMap
    participant Freq as freqMap (Frequency Lists)
    participant MinTracker as minFrequency Tracker

    App->>Cache: get(Key)
    Cache->>Cache: Retrieve Node (currentFreq = 2)
    Note over Cache,Freq: Step 1: Remove Node from List 2
    Cache->>Freq: freqMap[2].removeNode(Node)
    
    Note over Cache,MinTracker: Step 2: Update minFrequency if List 2 is empty and minFrequency == 2
    Alt List 2 is empty and minFrequency == 2
        Cache->>MinTracker: Increment minFrequency = 3
    End

    Note over Cache,Freq: Step 3: Increment frequency and insert into List 3
    Cache->>Cache: Node.freq++ (now 3)
    Cache->>Freq: freqMap[3].addNode(Node)
    Cache-->>App: Return Value
```
