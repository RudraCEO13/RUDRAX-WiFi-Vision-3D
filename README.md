# RUDRAX WiFi Vision 3D

> **"See Your Wi-Fi. Understand Your Space."**  
> *Powered by RudraX Technologies Studio*  
> *Official Website: [rudraxstudio.freedev.app](https://rudraxstudio.freedev.app)*

---

## 1. Overview

**RUDRAX WiFi Vision 3D** is a native Android application designed to visualize and audit Wi-Fi radio frequency (RF) signal propagation across physical indoor spaces. It bridges real-time Android system telemetry (`WifiManager`, `ConnectivityManager`) with **CameraX** live rear-view video, dynamic **3D spatial HUD overlays**, and interactive beacon placement.

The application adheres strictly to engineering honesty, scientific transparency, and Android privacy constraints. It processes all sensor and video data on-device without remote telemetry or cloud leakage.

---

## 2. Core Architecture & Feature Matrix

| Feature | Description | Implementation |
|---|---|---|
| **Connection Gate** | Verifies Wi-Fi power and association state before unlocking scanner | Native Intent to `Settings.ACTION_WIFI_SETTINGS` |
| **AR 3D Signal Scanner** | Live rear-camera preview with 3D holographic radar & spatial beacon pins | `androidx.camera:camera-camera2`, Jetpack Compose Canvas |
| **3D Simulation Fallback** | Lightweight interactive 3D perspective cyber grid for devices without camera | Custom Canvas matrix projection |
| **Room-by-Room Testing** | Multi-sample 6-second average RSSI tests with location labels | Coroutine sampling engine |
| **Persistent Storage** | Saves room scan history and rankings | SQLite / Android Room (`@Database`, `@Entity`, `@Dao`) |
| **Room Comparison** | Visual comparison chart highlighting strongest and weakest locations | Ranked bar chart + executive summary |
| **Nearby Spectrum Scan** | Scans AP channels, frequencies (2.4/5/6 GHz), and security suites | `WifiManager.scanResults` |
| **Local Network Audit** | Bounded subnet reachability probe detecting active IPs and Gateway | Concurrency-limited socket & ICMP probe |
| **Router Admin Link** | Direct browser handoff to router management gateway (`http://<gateway>`) | `Intent.ACTION_VIEW` |
| **Internet Latency Test** | Genuine DNS resolution and HTTP round-trip ping | Direct WAN probe via OkHttp |
| **15-Section Manual** | Complete documentation, user guide, and legal notice | Expandable interactive cards |

---

## 3. Official Brand Identity & Attribution

- **Application Name**: `RUDRAX WiFi Vision 3D`
- **Brand Owner**: RudraX Technologies Studio
- **Official Logo**: Cached from `https://rudraxstudio.freedev.app/RudraX-Technologies-Studio.jpg` with offline fallback
- **Legal Notice**:
  > *RUDRAX WiFi Vision 3D is an application developed and presented by RudraX Technologies Studio.*  
  > *© 2026 RudraX Technologies Studio. All rights reserved.*

---

## 4. Building the Application

### Prerequisites
- Android Studio Hedgehog / Iguana / Ladybug or newer
- Android SDK 36 (Minimum SDK: 24, Target SDK: 36)
- JDK 17 or JDK 21

### Building Debug APK via Command Line
```bash
gradle assembleDebug
```
Output APK location:
`app/build/outputs/apk/debug/app-debug.apk`

### Running Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

---

## 5. Security & Privacy Commitments

1. **Local Processing**: All Wi-Fi measurements, RSSI calculations, and camera previews are processed strictly in RAM and local SQLite database on your device.
2. **Zero Surveillance**: Camera feed is never recorded, encoded, or transmitted to any remote server.
3. **No Credential Harvesting**: The app never asks for or stores router administrator passwords or Wi-Fi network keys.
4. **Data Purge**: Users can delete individual room scans, clear all database records, or purge cached logo assets anytime in the About section.
