# Mobile System Design: Push Notification Delivery Pipeline

This system design document maps the lifecycle of remote push notifications, from backend triggers to client-side presentation layers.

---

## 1. End-to-End Delivery Flow

Push notifications route payloads to mobile devices using Apple Push Notification service (APNS) or Firebase Cloud Messaging (FCM) to conserve cellular radios and device battery:

```mermaid
sequenceDiagram
    participant User as User Action
    participant Server as Application Server
    participant Gateway as Push Gateway (APNS / FCM)
    participant OS as Device Operating System (iOS / Android)
    participant Ext as Notification Service Extension
    participant App as Client Application

    User->>Server: Action (e.g., Post message)
    Server->>Gateway: POST Push Payload (Token, encrypted text)
    Gateway-->>OS: Deliver low-power packet
    
    rect rgb(200, 220, 240)
        Note over OS,Ext: Background Decryption & Enrichment (Service Extension)
        OS->>Ext: Spawn Extension Process & Pass Payload
        Ext->>Ext: Decrypt payload using stored hardware key
        Ext->>Ext: Download dynamic media attachments (e.g. avatar)
        Ext-->>OS: Hand back modified Notification Layout
    end
    
    OS-->>User: Draw Visual Banner
    OS->>App: (If clicked) Launch app & resolve deep link
```

---

## 2. Low-Power Wakeups & Data Pushes

To update client state silently before the user unlocks the screen:
* **Silent Push Notifications**: Contain no visual alert values (`title`, `body`). Upon receipt, the OS wakes the client background process and grants a short execution window ($30$ seconds on iOS) to perform DB syncing, outbox cleaning, or telemetry flushing.
* **Throttling Rules**: The OS monitors silent push volume. Excessive silent notifications will trigger background execution blocks to protect battery health.

---

## 3. Security: Client-Side Decryption

To support absolute end-to-end security (e.g. Signal, WhatsApp):
1. **Encrypted Payloads**: The push gateway only receives encrypted text.
2. **Service Extension Isolation**: On receipt, the OS runs a lightweight **Notification Service Extension** (iOS) or Background Worker (Android). The extension retrieves the private key from the device's secure hardware store (Keychain/Keystore), decrypts the payload in memory, and presents the clean text to the user. The plaintext never touches the push gateway or persistent server logs.
