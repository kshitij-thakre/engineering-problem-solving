# Mobile Engineering: Pagination & Prefetching

## Context
When displaying list feeds containing thousands of elements (e.g. products inside a shop view or posts inside a chat history), loading all elements in a single network request blocks the application server and fills up the mobile device heap, triggering Out Of Memory (OOM) crashes. Mobile platforms implement dynamic list paging.

---

## Architecture Blueprints

Refer to the complete system architecture and detailed comparison between Offset-based and Cursor-based pagination inside [system-design-mobile/pagination-system.md](../../system-design-mobile/pagination-system.md).

---

## Code Example: Cursor Range Indexing (Prefetching calculations)

This algorithm calculates if a scroll index has crossed the prefetch threshold boundary to trigger a network request.

### Dart
```dart
class PaginationPrefetcher {
  final int pageSize;
  final double thresholdPercentage; // e.g., 0.8 (80%)
  
  bool _isLoading = false;

  PaginationPrefetcher({this.pageSize = 20, this.thresholdPercentage = 0.8});

  bool shouldFetchNextPage(int currentScrollIndex, int totalItemsLoaded) {
    if (_isLoading) return false;

    // Trigger next fetch when the user scrolls past the threshold (e.g. index 16 out of 20 loaded)
    int thresholdIndex = (totalItemsLoaded * thresholdPercentage).toInt();
    
    if (currentScrollIndex >= thresholdIndex) {
      return true;
    }
    return false;
  }

  void setLoading(bool loading) {
    _isLoading = loading;
  }
}
```

### Kotlin
```kotlin
class PaginationPrefetcher(
    val pageSize: Int = 20,
    val thresholdPercentage: Double = 0.8
) {
    private var isLoading = false

    fun shouldFetchNextPage(currentScrollIndex: Int, totalItemsLoaded: Int): Boolean {
        if (isLoading) return false

        val thresholdIndex = (totalItemsLoaded * thresholdPercentage).toInt()
        return currentScrollIndex >= thresholdIndex
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
    }
}
```
