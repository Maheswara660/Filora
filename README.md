<p align="center">
  <img src="assets/app_icon.webp" width="140" height="140" alt="Filora Logo">
</p>

<h1 align="center">Filora</h1>

<p align="center">
  <b>Elevate your digital life with Filora — a premium, high-performance File Explorer for Android.</b><br>
  Built with the precision of Jetpack Compose and the elegance of Material 3.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Language-Kotlin%202.1.0-blue?style=for-the-badge&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose" alt="Compose">
  <img src="https://img.shields.io/badge/Android-15-3DDC84?style=for-the-badge&logo=android" alt="Android">
</p>

---

## 📝 About Filora
Filora is more than just a file manager; it is a meticulously crafted productivity suite designed for the modern Android ecosystem. It combines raw power with a "Premium Airy" design philosophy, featuring glassmorphism and smooth micro-animations. Whether you are a power user managing complex directory structures or a developer writing code on the go, Filora provides an immersive and efficient experience.

---

## 🛠️ Technology Stack
Filora is built on a foundation of the latest industry-standard technologies to ensure stability, speed, and security.

| Component | Technology | Version | Description |
| :--- | :--- | :--- | :--- |
| **Core Language** | Kotlin | `2.1.0` | Modern, safe, and interoperable. |
| **UI Framework** | Jetpack Compose | `2.1.0` | Declarative UI for a fluid experience. |
| **Media Engine** | Media3 (ExoPlayer) | `1.8.0` | Industry-leading media playback. |
| **Image Loading** | Coil | `3.3.0` | Fast, hardware-accelerated image decoding. |
| **Code Editor** | Sora Editor | `0.23.6` | Professional-grade text rendering engine. |
| **Data Storage** | DataStore | `1.1.7` | Modern, asynchronous preferences storage. |
| **Architecture** | MVVM | — | Clean separation of concerns and logic. |

---

## ✨ Key Features

### 📂 Advanced Explorer
*   **Universal Search**: Instant, throttled search that scans Internal, SD Card, and OTG storage.
*   **Virtual Containers**: dedicated spaces for Recent files, Bookmarks, and Pinned items.
*   **Smart Categories**: One-tap access to Images, Videos, Audio, Documents, and APKs.
*   **Recycle Bin**: Professional safety net with easy recovery and permanent deletion options.

### 🎥 Multimedia Powerhouse
*   **Audio Player**: Full ID3 metadata extraction, dynamic background playback, and system media controls.
*   **Video Player**: Gesture-based controls (volume/brightness), multi-track audio support, and smooth scaling.
*   **Image Viewer**: High-performance viewing with hardware acceleration and deep zoom support.
*   **PDF Reader**: Seamless document viewing without needing external applications.

### 💻 Developer Tools
*   **Sora Code Editor**: Integrated editor with syntax highlighting, line numbering, and custom themes for various programming languages.
*   **App Manager**: Deep inspection of installed apps, including versioning and package management.
*   **Storage Analyzer**: Visual breakdown of disk usage to help you identify and reclaim space.

---

## 📸 Screenshots

<table align="center">
  <tr>
    <td align="center"><b>Home Screen</b></td>
    <td align="center"><b>File Explorer</b></td>
    <td align="center"><b>App Manager</b></td>
  </tr>
  <tr>
    <td><img src="assets/homescreen.png" width="220" style="border-radius: 12px; border: 1px solid #ddd;"></td>
    <td><img src="assets/browse.png" width="220" style="border-radius: 12px; border: 1px solid #ddd;"></td>
    <td><img src="assets/Apps_Screen.png" width="220" style="border-radius: 12px; border: 1px solid #ddd;"></td>
  </tr>
</table>

<br>

<table align="center">
  <tr>
    <td align="center"><b>Image Viewer</b></td>
    <td align="center"><b>Music Player</b></td>
    <td align="center"><b>Video Player</b></td>
  </tr>
  <tr>
    <td><img src="assets/Image_Viewer.png" width="220" style="border-radius: 12px; border: 1px solid #ddd;"></td>
    <td><img src="assets/Music_player.png.jpg" width="220" style="border-radius: 12px; border: 1px solid #ddd;"></td>
    <td><img src="assets/Video_Player.png" width="220" style="border-radius: 12px; border: 1px solid #ddd;"></td>
  </tr>
</table>


---

## 🚀 Installation & Requirements

### System Requirements
* **OS**: Android 8.0 (Oreo) or higher.
* **Architecture**: Supported on `arm64-v8a`, `armeabi-v7a`, `x86`, and `x86_64`.
* **Hardware**: Optimized for both high-end and entry-level devices.

### Permissions Explained
To provide a complete experience, Filora requires:
* **All Files Access**: To manage and organize files across your storage partitions.
* **Install Packages**: To allow the App Manager to install or update applications.
* **Notifications**: For background media playback controls.

### How to Install
1. Download the latest APK from the **[Releases](https://github.com/Maheswara660/Filora/releases)**.
2. Enable "Install from Unknown Sources" in Android Settings.
3. Launch Filora and grant the necessary permissions.

---

## 🏗️ Build from Source
Ensure you have **Android Studio Ladybug** and **JDK 17** configured.

```bash
# Clone the repository
git clone https://github.com/Maheswara660/Filora.git

# Enter the project directory
cd Filora

# Build the release variant
./gradlew assembleRelease
```

---

## ❤️ Support & Community
Filora is a labor of love by a **solo developer**. Your support directly fuels the development of new features!

* ⭐ **Star**: Please give this project a star if you find it useful.
* ☕ **[Buy me a coffee](https://ko-fi.com/Maheswara660)**: Support my work via Ko-fi.
* 🤝 **Contribute**: Check out the [Contributing Guidelines](.github/CONTRIBUTING.md).

---

## 🙏 Acknowledgements
Special thanks to the open-source community for the incredible libraries that power Filora:
* **Jetpack Compose & Media3 teams** at Google.
* **Rosemoe** for the powerful Sora Editor.
* **Coil-kt** for the seamless image engine.
* **Anggrayudi** for the comprehensive Storage library.

---

## 📜 License
Filora is open-source software licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for more information.

---

## ✉️ Message from Developer
> "Filora was born out of a desire for a file manager that feels professional yet looks beautiful. Every line of code and every UI component has been crafted to provide the best possible experience on Android. I hope Filora becomes an essential part of your daily workflow."
> — **Maheswara660**

<p align="center">
  <b>Filora — Experience Files Differently.</b><br>
  Made with ❤️ in India
</p>