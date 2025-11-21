# 📱 Panduan Penggunaan ME Android Sunset

## 📥 Download & Instalasi

### Cara 1: Download dari GitHub Releases (Recommended)

1. **Buka halaman Releases**
   - Kunjungi: https://github.com/danophelia857-stack/me-android-sunset/releases
   - Pilih release terbaru

2. **Download APK**
   - Download file `app-release.apk` (untuk penggunaan normal)
   - Atau `app-debug.apk` (untuk testing/development)

3. **Install APK**
   - Buka file APK yang sudah didownload
   - Jika muncul peringatan "Install from Unknown Sources":
     - Buka **Settings** → **Security** → **Unknown Sources**
     - Atau **Settings** → **Apps** → **Special Access** → **Install Unknown Apps**
     - Aktifkan untuk browser/file manager yang digunakan
   - Tap **Install**
   - Tunggu hingga instalasi selesai
   - Tap **Open** untuk membuka aplikasi

### Cara 2: Build dari Source Code

```bash
# Clone repository
git clone https://github.com/danophelia857-stack/me-android-sunset.git
cd me-android-sunset

# Build APK
./gradlew assembleRelease

# APK akan tersedia di:
# app/build/outputs/apk/release/app-release.apk
```

## 🔐 Login

### Langkah-langkah Login:

1. **Buka aplikasi** ME Android Sunset
2. **Masukkan Nomor HP**
   - Format: `628xxxxxxxxxx` (dimulai dengan 628)
   - Contoh: `628123456789`
3. **Masukkan Kode OTP**
   - Dapatkan OTP dari SMS atau aplikasi resmi
   - Masukkan 6 digit kode OTP
4. **Tap tombol Login**
5. **Tunggu proses autentikasi**
   - Aplikasi akan melakukan enkripsi offline
   - Tidak ada data yang dikirim ke server pihak ketiga

### Troubleshooting Login:

- **"Login gagal"**: Pastikan nomor HP dan OTP benar
- **"Network error"**: Periksa koneksi internet
- **"Invalid credentials"**: Minta OTP baru dan coba lagi

## 🏠 Halaman Utama (Home)

Setelah login berhasil, Anda akan melihat:

### Informasi Profil
- **Nomor HP**: Nomor yang sedang aktif
- **Type**: PREPAID atau POSTPAID
- **Pulsa**: Saldo pulsa tersisa
- **Aktif sampai**: Tanggal masa aktif
- **Points & Tier**: (untuk PREPAID) Poin loyalitas dan tier

### Menu Utama

#### 1. 📦 Lihat Paket Saya
- Melihat semua paket yang sedang aktif
- Detail kuota, masa berlaku, dan status

#### 2. ⭐ Beli Paket HOT
- Paket populer dan promo terkini
- Paket yang sering dibeli pengguna
- Harga spesial dan diskon

#### 3. 🔍 Cari Paket
- Cari paket berdasarkan:
  - **Option Code**: Kode paket spesifik
  - **Family Code**: Kategori paket
- Filter berdasarkan harga, kuota, masa berlaku

#### 4. 📊 Riwayat Transaksi
- Lihat semua transaksi pembelian
- Status: Berhasil, Pending, Gagal
- Detail paket dan waktu pembelian

#### 5. 👨‍👩‍👧 Family Plan
- Kelola family plan/Akrab Organizer
- Lihat anggota family
- Berbagi kuota dengan anggota

#### 6. 👥 Circle
- Kelola circle/grup
- Benefit khusus untuk circle
- Manajemen anggota circle

#### 7. ❤️ Bookmark
- Simpan paket favorit
- Akses cepat ke paket yang sering dibeli
- Tambah/hapus bookmark

## 💳 Membeli Paket

### Cara Beli Paket:

1. **Pilih Menu** (HOT, Cari Paket, dll)
2. **Browse Paket** yang tersedia
3. **Pilih Paket** yang diinginkan
4. **Lihat Detail**:
   - Nama paket
   - Kuota (data, SMS, telpon)
   - Harga
   - Masa berlaku
   - Syarat & ketentuan
5. **Konfirmasi Pembelian**
6. **Pilih Metode Pembayaran**:
   - Pulsa
   - E-wallet (jika tersedia)
   - QRIS
7. **Konfirmasi Pembayaran**
8. **Tunggu Proses**
   - Enkripsi dilakukan offline
   - Request dikirim ke server
9. **Paket Aktif!**
   - Notifikasi berhasil
   - Paket langsung aktif

### Tips Membeli Paket:

- ✅ Pastikan saldo pulsa cukup
- ✅ Periksa masa berlaku paket
- ✅ Baca syarat & ketentuan
- ✅ Simpan paket favorit di Bookmark
- ✅ Gunakan paket HOT untuk promo terbaik

## 🔖 Mengelola Bookmark

### Menambah Bookmark:

1. Buka detail paket
2. Tap icon **Bookmark** (⭐)
3. Paket tersimpan di menu Bookmark

### Menghapus Bookmark:

1. Buka menu **Bookmark**
2. Swipe paket ke kiri
3. Tap **Hapus**

### Membeli dari Bookmark:

1. Buka menu **Bookmark**
2. Tap paket yang diinginkan
3. Langsung ke halaman pembelian

## 👥 Family Plan & Circle

### Family Plan (Akrab Organizer):

**Sebagai Organizer:**
- Buat family plan baru
- Undang anggota (maksimal sesuai paket)
- Kelola kuota bersama
- Monitor penggunaan anggota

**Sebagai Member:**
- Terima undangan dari organizer
- Gunakan kuota bersama
- Lihat sisa kuota family

### Circle:

- Bergabung dengan circle
- Dapatkan benefit khusus
- Promo eksklusif untuk circle
- Berbagi informasi paket

## 🔄 Refresh Data

Untuk memperbarui data (saldo, paket, dll):

1. Tap icon **Refresh** (🔄) di pojok kanan atas
2. Atau **Pull to Refresh** di halaman utama
3. Data akan diperbarui otomatis

## 🚪 Logout

Untuk keluar dari aplikasi:

1. Tap icon **Logout** (🚪) di pojok kanan atas
2. Konfirmasi logout
3. Anda akan kembali ke halaman login

### Multi-Account:

- Aplikasi mendukung multiple accounts
- Ganti akun di menu **Account**
- Data setiap akun tersimpan terpisah

## 🔐 Keamanan & Privacy

### Enkripsi Offline:

- ✅ **AES-256 CBC**: Enkripsi data payload
- ✅ **HMAC-SHA512**: Signature generation
- ✅ **SHA-256**: IV derivation
- ✅ **Tidak ada server pihak ketiga**
- ✅ **Kunci enkripsi tersimpan lokal**

### Data yang Disimpan:

- Token autentikasi (encrypted)
- Informasi profil
- Bookmark paket
- Riwayat transaksi (cache)

### Data yang TIDAK Disimpan:

- ❌ Password/OTP
- ❌ Data pembayaran
- ❌ Informasi sensitif lainnya

### Backup & Restore:

- Data tersimpan di SharedPreferences
- Tidak di-backup ke cloud (untuk keamanan)
- Hapus data: **Settings** → **Apps** → **ME Sunset** → **Clear Data**

## ⚙️ Pengaturan

### Mengubah Tema:

- Aplikasi mengikuti tema sistem
- **Light Mode**: Tema terang
- **Dark Mode**: Tema gelap
- Ubah di **Settings** → **Display** → **Theme**

### Notifikasi:

- Aktifkan notifikasi untuk:
  - Paket hampir habis
  - Promo baru
  - Status transaksi
- Kelola di **Settings** → **Apps** → **ME Sunset** → **Notifications**

### Izin Aplikasi:

- **Internet**: Untuk komunikasi dengan API
- **Network State**: Untuk cek koneksi
- Tidak memerlukan izin lain

## 🐛 Troubleshooting

### Aplikasi Crash/Force Close:

1. **Clear Cache**:
   - **Settings** → **Apps** → **ME Sunset** → **Clear Cache**
2. **Clear Data** (akan logout):
   - **Settings** → **Apps** → **ME Sunset** → **Clear Data**
3. **Reinstall**:
   - Uninstall aplikasi
   - Download APK terbaru
   - Install ulang

### Koneksi Gagal:

- Periksa koneksi internet
- Coba ganti jaringan (WiFi ↔ Mobile Data)
- Restart aplikasi

### Paket Tidak Muncul:

- Tap **Refresh** di halaman utama
- Logout dan login kembali
- Periksa masa berlaku paket

### Pembelian Gagal:

- Pastikan saldo cukup
- Periksa koneksi internet
- Coba beberapa saat lagi
- Hubungi customer service jika masalah berlanjut

## 📞 Dukungan

### Laporkan Bug:

- Buka issue di GitHub: https://github.com/danophelia857-stack/me-android-sunset/issues
- Sertakan:
  - Versi aplikasi
  - Model HP & versi Android
  - Screenshot error (jika ada)
  - Langkah-langkah reproduksi

### Request Fitur:

- Buka discussion di GitHub
- Jelaskan fitur yang diinginkan
- Berikan use case/contoh penggunaan

### Kontribusi:

- Fork repository
- Buat branch baru
- Commit perubahan
- Submit pull request

## 📝 Catatan Penting

### Disclaimer:

⚠️ **Aplikasi ini untuk tujuan edukasi**. Pengguna bertanggung jawab atas penggunaan aplikasi sesuai hukum yang berlaku.

### Kepatuhan:

- Gunakan sesuai Terms of Service provider
- Jangan menyalahgunakan untuk aktivitas ilegal
- Hormati kebijakan fair usage

### Update:

- Cek update rutin di GitHub Releases
- Update otomatis tidak tersedia (install manual)
- Backup data sebelum update

## 🎉 Tips & Trik

1. **Bookmark Paket Favorit**: Akses cepat untuk pembelian rutin
2. **Cek Promo HOT**: Paket promo dengan harga terbaik
3. **Monitor Saldo**: Set reminder untuk isi pulsa
4. **Family Plan**: Hemat dengan berbagi kuota
5. **Dark Mode**: Hemat baterai di malam hari

## 📊 FAQ

**Q: Apakah aplikasi ini aman?**  
A: Ya, semua enkripsi dilakukan offline tanpa server pihak ketiga.

**Q: Apakah data saya aman?**  
A: Data tersimpan lokal dan tidak di-backup ke cloud.

**Q: Apakah bisa multi-account?**  
A: Ya, aplikasi mendukung multiple accounts.

**Q: Bagaimana cara update aplikasi?**  
A: Download APK terbaru dari GitHub Releases dan install.

**Q: Apakah aplikasi ini gratis?**  
A: Ya, aplikasi ini open source dan gratis.

**Q: Apakah ada iklan?**  
A: Tidak, aplikasi ini bebas iklan.

**Q: Apakah perlu root?**  
A: Tidak, aplikasi berjalan tanpa root.

**Q: Minimum Android version?**  
A: Android 7.0 (API 24) atau lebih baru.

---

**Selamat menggunakan ME Android Sunset! 🌅**

Untuk pertanyaan lebih lanjut, silakan buka issue di GitHub atau hubungi developer.
