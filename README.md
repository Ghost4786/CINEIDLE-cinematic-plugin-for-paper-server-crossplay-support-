# CineIdle

**A next-generation Cinematic AFK plugin for Paper 1.21+**  
*Developed by GHOST4786*

---

CineIdle completely overhauls the standard AFK experience. Instead of just standing completely still, players who go AFK will be seamlessly transitioned into a gorgeous, fully-automated cinematic camera mode that orbits their player model in real-time. 

## Features

### Core Mechanics
* **10 Unique Choreographed Angles:** Includes Drone Drops, Helix Orbitals, Ground Pans, and more.
* **Packet-Based NPC Clones:** Your real body stays completely safe while an identical NPC clone sleeps on the ground for other players to see.
* **Cross-Play Bedrock Support:** Fully integrates with Geyser and Floodgate to seamlessly hijack Bedrock cameras without any jitter.

### Technical Optimizations
* **Zero-Jitter Ping Compensation:** Extremely strict latency detection ensures you instantly wake up the millisecond you touch your mouse or WASD keys.
* **Smart Collision Raytracing:** The camera shoots an invisible laser to your eye line, guaranteeing the camera never clips through walls or gets stuck in a corner.
* **100% Memory Leak Free:** Carefully designed to wipe all ghost packets and maps immediately upon player disconnect.

---

## Installation Guide

### Setup
1. Download the latest `CineIdle.jar` release.
2. Place the file inside your server's `plugins/` directory.
3. Restart your server.

### Dependencies
* **Paper 1.21+** (Folia is currently not supported)
* **ProtocolLib** (Required - Must be v5.3.0 or higher)
* **Geyser & Floodgate** (Optional - Only required if you want Bedrock cross-play support)

---

## Configuration

When the server starts, a `config.yml` will be generated in `plugins/CineIdle/`:

```yaml
# ==========================================
# CineIdle Configuration
# Developed by GHOST4786
# ==========================================

# How long a player must be inactive before the cinematic starts (in seconds)
# Default: 300 (5 minutes)
afk-trigger-time-seconds: 300

# How far away the camera should orbit from the player
# Increase this value to make the camera zoom out further
# Default: 1.0
camera-distance-multiplier: 1.0

# How long each cinematic camera shot should last before switching to the next angle (in ticks)
# 20 ticks = 1 second. Default: 1600 (80 seconds)
transition-speed-ticks: 1600
```

---

## Usage & Mechanics

### Commands & Permissions

#### `/forceafk [player]` 
* **Permission:** `cineidle.force` (Op by default)
* **Description:** Forces a player (or yourself) immediately into the cinematic AFK state. Great for testing configuration changes or recording trailers.

### Waking Up
A player can exit the cinematic AFK state at any time by performing *any* of the following physical actions:
* Moving the mouse or using WASD.
* Pressing Shift (Sneak).
* Swapping items (F), dropping items (Q), or switching hotbar slots (Scroll).
* Left-clicking or Right-clicking.
* Typing in chat.

---

## License & Copyright

**© 2026 GHOST4786. All Rights Reserved.**

This project is **CLOSED SOURCE**. Although the repository is public for portfolio and display purposes, you are **strictly prohibited** from copying, modifying, distributing, or using this source code on any server without explicit written permission from GHOST4786.

See the [LICENSE](LICENSE) file for more details.
