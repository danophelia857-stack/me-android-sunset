# ME Android Sunset 🌅

[![Android CI/CD](https://github.com/YOUR_USERNAME/me-android-sunset/actions/workflows/android-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/me-android-sunset/actions/workflows/android-build.yml)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)

Aplikasi Android native untuk provider internet mobile Indonesia dengan **enkripsi offline penuh** tanpa ketergantungan server eksternal.

## ✨ Fitur Utama

- 🔐 **Full Offline Encryption**: Enkripsi AES-256 CBC dilakukan sepenuhnya di perangkat
- 🚀 **No External Server**: Tidak ada server pihak ketiga yang digunakan untuk enkripsi
- 📱 **Material 3 Design**: UI modern dengan Jetpack Compose
- 💳 **Package Management**: Beli dan kelola paket data
- 📊 **Balance & History**: Cek saldo dan riwayat transaksi
- 👥 **Family Plan**: Kelola family plan dan circle
- 🔖 **Bookmarks**: Simpan paket favorit
- 🌙 **Dark Mode**: Mendukung tema gelap dan terang

## 🏗️ Arsitektur

Aplikasi ini dibangun menggunakan:

- **Kotlin**: Bahasa pemrograman modern untuk Android
- **Jetpack Compose**: UI toolkit deklaratif
- **Material 3**: Design system terbaru dari Google
- **Retrofit**: HTTP client untuk API calls
- **Coroutines**: Asynchronous programming
- **ViewModel**: State management
- **DataStore**: Persistent storage
- **Navigation Compose**: In-app navigation

## 🔒 Keamanan

### Enkripsi Offline

Semua operasi enkripsi dilakukan di perangkat menggunakan algoritma standar:

- **AES-256 CBC**: Untuk enkripsi data payload
- **HMAC-SHA512**: Untuk signature generation
- **HMAC-SHA256**: Untuk API signature
- **SHA-256**: Untuk IV derivation

### Implementasi Kriptografi

```kotlin
// Contoh enkripsi xdata
fun encryptXData(plaintext: String, xtimeMs: Long): String {
    val iv = deriveIv(xtimeMs) // SHA-256 hash dari timestamp
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
    return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray()), Base64.URL_SAFE)
}
```

Tidak ada kunci enkripsi yang dikirim ke server eksternal. Semua kunci disimpan secara aman di dalam aplikasi.

## 📦 Download & Instalasi

### Dari GitHub Releases

1. Buka [Releases](https://github.com/YOUR_USERNAME/me-android-sunset/releases)
2. Download file **app-release.apk** dari release terbaru
3. Aktifkan "Install from Unknown Sources" di pengaturan Android
4. Install APK
5. Buka aplikasi dan login

### Build dari Source

```bash
# Clone repository
git clone https://github.com/YOUR_USERNAME/me-android-sunset.git
cd me-android-sunset

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# APK akan tersedia di:
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

## 🚀 CI/CD

Project ini menggunakan **GitHub Actions** untuk automated building:

- ✅ Automatic build on push to main/master
- ✅ Automatic build on pull requests
- ✅ Upload artifacts (APK files)
- ✅ Create GitHub releases with APK attachments
- ✅ Manual workflow dispatch

### Workflow Triggers

```yaml
on:
  push:
    branches: [ main, master, develop ]
  pull_request:
    branches: [ main, master ]
  workflow_dispatch:
```

## 📱 Screenshots

| Login Screen | Home Screen | Package List |
|-------------|-------------|--------------|
| ![Login](screenshots/login.png) | ![Home](screenshots/home.png) | ![Packages](screenshots/packages.png) |

## 🛠️ Development

### Requirements

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34
- Gradle 8.2

### Setup Development Environment

1. Clone repository
2. Buka project di Android Studio
3. Sync Gradle files
4. Run pada emulator atau device fisik

### Project Structure

```
me-android-sunset/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/mashu/mesunset/
│   │       │   ├── crypto/          # Enkripsi & kriptografi
│   │       │   ├── data/            # Models, API, Repository
│   │       │   ├── ui/              # Screens & ViewModels
│   │       │   └── MainActivity.kt
│   │       ├── res/                 # Resources (layouts, strings, etc)
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── .github/
│   └── workflows/
│       └── android-build.yml        # CI/CD workflow
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚠️ Disclaimer

This application is for educational purposes only. By using this tool, the user agrees to comply with all applicable laws and regulations and to release the developer from any and all claims arising from its use.

## 🙏 Credits

- Original CLI version: [me-cli-sunset](https://github.com/purplemashu/me-cli-sunset)
- Inspired by the need for transparent, offline encryption
- Built with ❤️ using Kotlin & Jetpack Compose

## 📧 Contact

For questions or support, please open an issue on GitHub.

---

**Note**: Replace `YOUR_USERNAME` with your actual GitHub username in the URLs above.
