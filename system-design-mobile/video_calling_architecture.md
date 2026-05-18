# Mobile System Design: WebRTC P2P Video Calling Architecture

This system design document details the peer-to-peer WebRTC connection handshake, network traversal, and low-latency audio/video media pipelines on mobile clients.

---

## 1. Signaling & WebRTC Peer Handshake

WebRTC enables real-time peer-to-peer (P2P) media streaming. However, because mobile devices sit behind firewalls and dynamic NATs, peers cannot discover each other directly. We utilize a **Signaling Channel** (typically using WebSockets) to negotiate connection metadata:

```mermaid
sequenceDiagram
    participant PeerA as Mobile Client A
    participant Sig as Signaling Server
    participant PeerB as Mobile Client B

    PeerA->>Sig: POST SDP Offer (supported codecs, capabilities)
    Sig->>PeerB: Forward SDP Offer
    PeerB->>PeerB: Set Remote Description
    PeerB->>Sig: POST SDP Answer
    Sig->>PeerA: Forward SDP Answer
    PeerA->>PeerA: Set Remote Description

    Note over PeerA,PeerB: ICE Candidate Signaling (Dynamic Network Routes)
    PeerA->>Sig: Send ICE Candidates
    Sig->>PeerB: Forward ICE Candidates
    PeerB->>Sig: Send ICE Candidates
    Sig->>PeerA: Forward ICE Candidates

    Note over PeerA,PeerB: Direct Low-Latency P2P Video Channel Established!
    PeerA<->>PeerB: RTCPeerConnection (SRTP Media Packets)
```

---

## 2. NAT Traversal: STUN vs. TURN

To establish direct socket connections through cell towers or home routers:
1. **STUN (Session Traversal Utilities for NAT)**:
   * **Purpose**: A lightweight server that tells the client its public IP address and port mapping. Resolves paths for ~80% of consumer routing topologies.
2. **TURN (Traversal Using Relays around NAT)**:
   * **Purpose**: If symmetric NAT firewalls block direct P2P connections, media must be relayed through a TURN proxy server.
   * **Cost**: Highly resource-intensive and expensive because all video bandwidth routes through the TURN cluster.

---

## 3. Media Pipelines: Echo Cancellation & Hard Rendering

* **Echo Cancellation (AEC)**: Audio streams capture speaker feedback back into the microphone, creating howling noise. Mobile clients bind WebRTC audio channels directly to platform hardware echo cancelers (AEC) and automatic gain controllers (AGC).
* **Hardware Rendering**: Raw video frames route directly through GPU OpenGL/Metal textures using native views (`AndroidView` on Flutter, `UiKitView` on iOS) to prevent main-thread copying lag.
