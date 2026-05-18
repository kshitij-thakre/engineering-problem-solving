# Pattern: Bit Manipulation

## Mobile Engineering Context
In mobile applications, storing and synchronizing state flags (e.g. system permissions granted, app feature toggles, or user settings configurations) in multiple database columns or distinct boolean variables is redundant and hard to scale. **Bit Manipulation** allows developers to pack multiple boolean flags into a single, compact **32-bit Integer** (supporting up to 32 independent boolean values) or a **64-bit Integer** (supporting 64).

---

## Real-World Mobile Relevance

### 1. Unified Permission Masks
* **How it works**: An app has 5 permissions: Location, Camera, Microphone, Contacts, and Storage. Rather than saving 5 columns in the SQLite database, we map each permission to a single bit position. Checking or updating permissions requires direct bitwise operations, keeping serialization payloads extremely tiny.

### 2. Network Header Packing
* **How it works**: Bluetooth low energy (BLE) and low-overhead HTTP protocols use bit fields to pack control signals into individual bytes, optimizing battery and packet travel times.

---

## Code Example: Binary Permission Flag Management

### Dart
```dart
class PermissionManager {
  static const int location   = 1 << 0; // 00001 (1)
  static const int camera     = 1 << 1; // 00010 (2)
  static const int microphone = 1 << 2; // 00100 (4)
  static const int contacts   = 1 << 3; // 01000 (8)
  
  int _mask = 0; // Initial state: No permissions granted

  // Grant a permission (Bitwise OR)
  void grant(int permission) {
    _mask |= permission;
  }

  // Revoke a permission (Bitwise AND with NOT)
  void revoke(int permission) {
    _mask &= ~permission;
  }

  // Check if a permission is granted (Bitwise AND)
  bool isGranted(int permission) {
    return (_mask & permission) != 0;
  }

  // Clear all permissions
  void clearAll() {
    _mask = 0;
  }

  int get mask => _mask;
}

void main() {
  final manager = PermissionManager();
  
  manager.grant(PermissionManager.location);
  manager.grant(PermissionManager.camera);
  
  print("Is Location granted? ${manager.isGranted(PermissionManager.location)}"); // true
  print("Is Microphone granted? ${manager.isGranted(PermissionManager.microphone)}"); // false
  print("Current permission mask value: ${manager.mask}"); // 3 (00011)

  manager.revoke(PermissionManager.location);
  print("Is Location granted after revoke? ${manager.isGranted(PermissionManager.location)}"); // false
}
```

### Kotlin
```kotlin
class PermissionManager {
    companion object {
        const val LOCATION = 1 shl 0   // 00001 (1)
        const val CAMERA = 1 shl 1     // 00010 (2)
        const val MICROPHONE = 1 shl 2 // 00100 (4)
        const val CONTACTS = 1 shl 3   // 01000 (8)
    }

    private var mask = 0 // Initial state

    fun grant(permission: Int) {
        mask = mask or permission
    }

    fun revoke(permission: Int) {
        mask = mask and permission.inv()
    }

    fun isGranted(permission: Int): Boolean {
        return (mask and permission) != 0
    }

    fun getMaskValue(): Int {
        return mask
    }
}

fun main() {
    val manager = PermissionManager()
    
    manager.grant(PermissionManager.LOCATION)
    manager.grant(PermissionManager.CAMERA)
    
    println("Is Location granted? ${manager.isGranted(PermissionManager.LOCATION)}") // true
    println("Is Microphone granted? ${manager.isGranted(PermissionManager.MICROPHONE)}") // false
    println("Current permission mask: ${manager.getMaskValue()}") // 3

    manager.revoke(PermissionManager.LOCATION)
    println("Is Location granted after revoke? ${manager.isGranted(PermissionManager.LOCATION)}") // false
}
```
