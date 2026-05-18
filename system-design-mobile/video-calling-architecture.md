# Mobile System Design: Video Calling & WebRTC Architecture

This document describes the detailed client-side system design for a real-time peer-to-peer (P2P) Video Calling application powered by WebRTC.

---

## 1. WebRTC Core Workflow

WebRTC (Web Real-Time Communication) allows direct, low-latency audio/video transmission between two mobile devices. However, direct P2P connection establishment requires a central signaling bridge to exchange connection metadata first.

```mermaid
sequenceDiagram
    participant PeerA as Client A (Caller)
    participant Signal as Signaling Server (WS/REST)
    participant STUN as STUN / TURN Server
    participant PeerB as Client B (Callee)

    PeerA->{STUN}: Query Public IP & Port (ICE Candidates)
    STUN-->>PeerA: Return ICE Candidate details
    PeerA->>Signal: Send OFFER (SDP) + ICE Candidates
    Signal->>PeerB: Forward OFFER (SDP) + ICE Candidates A
    PeerB->{STUN}: Query Public IP & Port
    STUN-->>PeerB: Return ICE Candidate details B
    PeerB->>Signal: Send ANSWER (SDP) + ICE Candidates B
    Signal->>PeerA: Forward ANSWER (SDP) + ICE Candidates B
    Note over PeerA,PeerB: Direct Peer-to-Peer WebRTC Data Channel Opened
    PeerA<->>PeerB: Secure RTP Video/Audio Stream (SRTP)
```

---

## 2. Signaling Handshake & Session Description Protocol (SDP)

The **Signaling Handshake** is the negotiation process where both devices agree on video formats, screen resolutions, supported audio codecs, and network routing properties:
* **Session Description Protocol (SDP)**: A text-formatted description containing video codecs (e.g. H.264, VP8, AV1), media parameters (e.g. $1080$p at $30$fps), and encryption protocols.
  * **Offer**: Sent by Client A containing A's SDP.
  * **Answer**: Returned by Client B containing B's SDP.

---

## 3. NAT Traversal: STUN vs. TURN

Most mobile phones are behind restrictive firewall gateways and Network Address Translation (NAT) routers, preventing direct P2P connections by default. WebRTC solves this using the **Interactive Connectivity Establishment (ICE)** protocol:

### 1. STUN (Session Traversal Utilities for NAT)
* **What It Does**: A STUN server simply tells the requesting device its public-facing IP address and port number.
* **Relevance**: Works for over 80% of normal residential and public connections, enabling direct, free peer-to-peer data streaming.

### 2. TURN (Traversal Using Relays around NAT)
* **What It Does**: If symmetric firewalls prevent direct P2P traffic, the TURN server acts as a **media relay**. Both peers stream their video/audio directly to the TURN server, which forwards it to the other peer.
* **Relevance**: Crucial backup (works in 100% of cases), but highly resource-expensive since it consumes massive backend bandwidth to route live video streams.

---

## 4. On-Device Rendering & Hardware Acceleration

Real-time video processing is highly resource-intensive and can cause thermal throttling on mobile devices.

### Local Camera Rendering Pipeline
1. **Camera Input Capture**: Capture raw image streams using platform camera APIs (`CameraX`/`Camera2` on Android, `AVFoundation` on iOS).
2. **GPU Texture Mapping**: Instead of converting raw camera frames to heavy JPEG/PNG objects in CPU memory, map the frames as OpenGL ES or Metal **GPU textures** directly.
3. **Texture Sharing**: Pass the GPU texture reference directly to the rendering engine (e.g. Flutter's `Texture` widget) to draw on screen instantly without crossing the language bridge, yielding a highly fluid 60fps local camera preview.

### Hardware Encoding & Decoding
* **Software Encoders (CPU)**: VP8/VP9 software codecs run compression in CPU threads, causing heavy battery drain and device heat.
* **Hardware Acceleration (GPU/ASIC)**: Modern chips feature dedicated hardware enablers for H.264/H.265. Enforcing hardware-accelerated video codecs keeps CPU utilization below 10%, preserving battery life.
