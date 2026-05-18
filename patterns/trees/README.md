# Pattern: Trees

## Mobile Engineering Context
Trees are hierarchical, non-linear data structures consisting of nodes connected by edges. In modern declarative mobile UI frameworks (Flutter, Jetpack Compose, Swift plans), **Trees** are the absolute foundation of UI layout architectures.

---

## Real-World Mobile Relevance

### 1. Flutter's Three Trees
* **Widget Tree**: Lightweight, blueprint tree.
* **Element Tree**: Structural logical tree that retains State nodes.
* **RenderObject Tree**: Concrete sizing, layout, and drawing tree.

### 2. UI Layout Tree Reconciliation (Diffing)
* **How it works**: When state changes, declarative frameworks do not destroy the entire view tree (which would cause massive layout costs). Instead, they run an efficient tree-traversal diffing algorithm ($O(N)$ runtime) to identify *only* the nodes that have changed and update their properties.

---

## Code Example: Binary Tree Depth-First Traversal (Pre-order, In-order, Post-order)

### Dart
```dart
class TreeNode {
  final String label;
  TreeNode? left;
  TreeNode? right;

  TreeNode(this.label);
}

class TreeTraversal {
  void preOrder(TreeNode? node) {
    if (node == null) return;
    print(node.label); // Process current node
    preOrder(node.left);
    preOrder(node.right);
  }
}
```

### Kotlin
```kotlin
class TreeNode(val label: String) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class TreeTraversal {
    fun preOrder(node: TreeNode?) {
        if (node == null) return
        println(node.label) // Process node
        preOrder(node.left)
        preOrder(node.right)
    }
}
```
