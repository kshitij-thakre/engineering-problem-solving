# Pattern: Tries (Prefix Tree)

## Mobile Engineering Context
Text input fields (search bars, address input, tag matching) are prominent in mobile interfaces. Querying a relational local SQL database like SQLite using `LIKE '%query%'` for autocomplete suggestions on every keystroke blocks the UI and drains resources. A **Trie** is a specialized tree structure that allows prefix-based autocomplete queries in $O(L)$ time, where $L$ is the length of the prefix.

---

## Real-World Mobile Relevance

### Autocomplete & Search Suggestions
* **How it works**: By loading a vocabulary of common search terms (e.g. 5,000 words) into an in-memory Trie on app startup, the search bar can return suggestions as the user types, completely offline and with zero database locks or network lag.

---

## Code Example: In-Memory Autocomplete Suggestions

### Dart
```dart
class TrieNode {
  final Map<String, TrieNode> children = {};
  bool isEndOfWord = false;
}

class AutocompleteTrie {
  final TrieNode root = TrieNode();

  void insert(String word) {
    TrieNode current = root;
    for (int i = 0; i < word.length; i++) {
      String char = word[i];
      current = current.children.putIfAbsent(char, () => TrieNode());
    }
    current.isEndOfWord = true;
  }

  // Find all words starting with the given prefix
  List<String> getSuggestions(String prefix) {
    TrieNode current = root;
    for (int i = 0; i < prefix.length; i++) {
      String char = prefix[i];
      if (!current.children.containsKey(char)) {
        return [];
      }
      current = current.children[char]!;
    }

    List<String> suggestions = [];
    _dfs(current, prefix, suggestions);
    return suggestions;
  }

  void _dfs(TrieNode node, String prefix, List<String> suggestions) {
    if (node.isEndOfWord) {
      suggestions.add(prefix);
    }
    node.children.forEach((char, childNode) {
      _dfs(childNode, prefix + char, suggestions);
    });
  }
}

void main() {
  final trie = AutocompleteTrie();
  trie.insert("flutter");
  trie.insert("flow");
  trie.insert("flight");
  trie.insert("kotlin");

  print(trie.getSuggestions("fl")); // [flutter, flow, flight]
  print(trie.getSuggestions("ko")); // [kotlin]
}
```

### Kotlin
```kotlin
class TrieNode {
    val children = HashMap<Char, TrieNode>()
    var isEndOfWord = false
}

class AutocompleteTrie {
    val root = TrieNode()

    fun insert(word: String) {
        var current = root
        for (char in word) {
            current = current.children.computeIfAbsent(char) { TrieNode() }
        }
        current.isEndOfWord = true
    }

    fun getSuggestions(prefix: String): List<String> {
        var current = root
        for (char in prefix) {
            current = current.children[char] ?: return emptyList()
        }
        val suggestions = ArrayList<String>()
        dfs(current, prefix, suggestions)
        return suggestions
    }

    private fun dfs(node: TrieNode, currentWord: String, suggestions: ArrayList<String>) {
        if (node.isEndOfWord) {
            suggestions.add(currentWord)
        }
        for ((char, childNode) in node.children) {
            dfs(childNode, currentWord + char, suggestions)
        }
    }
}

fun main() {
    val trie = AutocompleteTrie()
    trie.insert("flutter")
    trie.insert("flow")
    trie.insert("flight")
    trie.insert("kotlin")

    println(trie.getSuggestions("fl")) // [flutter, flow, flight]
    println(trie.getSuggestions("ko")) // [kotlin]
}
```
