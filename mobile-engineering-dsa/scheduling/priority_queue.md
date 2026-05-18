# Priority Queue Mechanics

## 1. Heap Structuring Properties
A **Priority Queue** is a abstract data type that behaves like a normal queue, but where each element holds an associated priority value. 

To implement a Priority Queue with logarithmic guarantees, we build a **Binary Heap**:
* **Complete Binary Tree**: A binary tree that is completely filled at all levels, except possibly the lowest level, which is filled from left to right.
* **Heap Invariant**:
  * **Max-Heap**: The value of each parent node is greater than or equal to the values of its children. The absolute maximum element is located at the root.
  * **Min-Heap**: The value of each parent node is less than or equal to the values of its children. The absolute minimum element is located at the root.

---

## 2. Array-Backed Representation

Because a binary heap is a complete binary tree, allocating node wrapper classes with pointers (left, right) is highly inefficient and creates heap memory fragmentation on mobile devices. Instead, we map the tree into a single, flat **contiguous Array** index layout:

```
               Node 0 (Root)
              /             \
         Node 1              Node 2
        /      \            /      \
     Node 3   Node 4     Node 5   Node 6
```

### Index Mapping Equations
For any Node stored at index `i` inside our array:
* **Parent Index**: `(i - 1) / 2` (Integer division)
* **Left Child Index**: `2 * i + 1`
* **Right Child Index**: `2 * i + 2`

---

## 3. Operations: Bubble Up & Bubble Down

### 1. Insertion (`siftUp` / Bubble Up)
1. Append the new element at the very end of the array (preserving the complete tree structure).
2. Compare the element with its parent. If it violates the heap invariant (e.g. its priority is higher than its parent in a Max-Heap), swap them.
3. Repeat steps 2-3 recursively traveling up until the invariant is satisfied.
* **Time Complexity**: $O(\log N)$ in the worst case (height of the tree).

### 2. Extraction (`siftDown` / Bubble Down)
1. Save the root element (located at index `0`) to return.
2. Replace the root element with the element at the very end of the array. Pop the last index.
3. Compare the new root element with its children. If it violates the heap invariant, swap it with the *larger* child (for Max-Heap) or *smaller* child (for Min-Heap).
4. Repeat step 3 traveling down recursively.
* **Time Complexity**: $O(\log N)$ in the worst case.
