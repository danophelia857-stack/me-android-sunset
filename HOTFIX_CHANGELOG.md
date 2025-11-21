# Hotfix Changelog - OTP Implementation

## Tanggal: 21 November 2025

### Perbaikan Utama: Logika Pengiriman OTP

#### 1. CryptoHelper.kt - Penambahan Fungsi Device Headers dan Timestamp

**Fungsi Baru:**
- `generateAxFingerprint(msisdn)`: Generate fingerprint terenkripsi dari device info
  - Format plaintext: `manufacturer|model|lang|resolution|tz_short|ip|font_scale|Android release|msisdn`
  - Enkripsi: AES-CBC dengan key AX_FP_KEY dan IV all-zeros
  - Output: Base64 encoded

- `generateAxDeviceId(msisdn)`: Generate MD5 hash dari fingerprint
  - Input: Fingerprint dari `generateAxFingerprint()`
  - Output: Hex string MD5 hash

- `generateJavaLikeTimestamp()`: Generate timestamp format Java
  - Format: `2023-10-20T12:34:56.78+07:00`
  - Timezone: GMT+7

- `generateGmt7Timestamp(subtractMinutes)`: Generate timestamp GMT+7 tanpa colon
  - Format: `2023-10-20T12:34:56.789+0700`
  - Parameter: `subtractMinutes` untuk mengurangi waktu (default 0)

- `generateUuid()`: Generate UUID v4

**Catatan:**
- Fingerprint dan Device ID di-cache untuk performa
- Menggunakan random manufacturer dan model untuk setiap instance

#### 2. ApiService.kt - Penambahan CIAM API Interface

**Interface Baru: CiamApiService**

Endpoint yang ditambahkan:
1. **Request OTP**
   - Method: GET
   - Path: `/realms/xl-ciam/auth/otp`
   - Query params: `contact`, `contactType`, `alternateContact`
   - Headers: Authorization, Ax-Device-Id, Ax-Fingerprint, Ax-Request-At, dll

2. **Submit OTP (Login)**
   - Method: POST
   - Path: `/realms/xl-ciam/protocol/openid-connect/token`
   - Content-Type: `application/x-www-form-urlencoded`
   - Form fields: `contactType`, `code`, `grant_type`, `contact`, `scope`
   - Headers: Authorization, Ax-Api-Signature, Ax-Device-Id, dll

3. **Extend Session**
   - Method: GET
   - Path: `/realms/xl-ciam/auth/extend-session`
   - Query params: `contact` (base64 encoded subscriber_id), `contactType`

4. **Refresh Token**
   - Method: POST
   - Path: `/realms/xl-ciam/protocol/openid-connect/token`
   - Form fields: `grant_type`, `refresh_token`

#### 3. Models.kt - Penambahan CIAM Response Models

**Model Baru:**
- `CiamOtpResponse`: Response dari request OTP
  - Fields: `subscriber_id`, `error`, `error_description`

- `CiamTokenResponse`: Response dari submit OTP / refresh token
  - Fields: `access_token`, `refresh_token`, `id_token`, `token_type`, `expires_in`, `error`, `error_description`

- `CiamExtendSessionResponse`: Response dari extend session
  - Fields: `data` (CiamExtendSessionData), `error`, `error_description`

- `CiamExtendSessionData`: Data dari extend session
  - Fields: `exchange_code`

#### 4. RetrofitClient.kt - Penambahan CIAM Retrofit Instance

**Perubahan:**
- Menambahkan `ciamRetrofit` dengan base URL `Constants.BASE_CIAM_URL`
- Menambahkan `ciamApiService` untuk akses CIAM endpoints
- Menggunakan OkHttpClient yang sama dengan timeout 30 detik

#### 5. MESunsetRepository.kt - Perbaikan Request OTP dan Login

**Fungsi `requestOtp(msisdn)` - Diperbaiki:**

**Sebelum:**
- Endpoint: `POST /api/v1/auth/otp/request` (SALAH!)
- Base URL: `https://api.myxl.xlaxiata.co.id/`
- Body: JSON dengan `contact`, `contact_type`, `grant_type`
- Headers: Hanya Authorization dan User-Agent

**Sesudah:**
- Endpoint: `GET /realms/xl-ciam/auth/otp` (BENAR!)
- Base URL: `https://gede.ciam.xlaxiata.co.id/`
- Query params: `contact`, `contactType=SMS`, `alternateContact=false`
- Headers lengkap:
  - `Authorization`: Basic {BASIC_AUTH}
  - `Ax-Device-Id`: MD5 hash dari fingerprint
  - `Ax-Fingerprint`: Encrypted device info
  - `Ax-Request-At`: Java-like timestamp
  - `Ax-Request-Id`: UUID v4
  - `Ax-Request-Device`: "samsung"
  - `Ax-Request-Device-Model`: "SM-N935F"
  - `Ax-Substype`: "PREPAID"
  - `User-Agent`: UA

**Validasi:**
- Nomor telepon harus dimulai dengan "628"
- Maksimal 14 digit

**Response:**
- Menyimpan `subscriber_id` untuk digunakan saat login

---

**Fungsi `login(msisdn, otp)` - Diperbaiki:**

**Sebelum:**
- Endpoint: `POST /api/v1/auth/login` (SALAH!)
- Base URL: `https://api.myxl.xlaxiata.co.id/`
- Content-Type: `application/json`
- Body: Encrypted JSON dengan xdata/xtime
- Signature: HMAC-SHA256 dari (timestamp + AX_FP_KEY)

**Sesudah:**
- Endpoint: `POST /realms/xl-ciam/protocol/openid-connect/token` (BENAR!)
- Base URL: `https://gede.ciam.xlaxiata.co.id/`
- Content-Type: `application/x-www-form-urlencoded`
- Body: Form-urlencoded dengan:
  - `contactType=SMS`
  - `code={otp}`
  - `grant_type=password`
  - `contact={msisdn}`
  - `scope=openid`
- Headers lengkap:
  - `Authorization`: Basic {BASIC_AUTH}
  - `Ax-Api-Signature`: HMAC-SHA256 dari ({timestamp}password{contactType}{contact}{code}openid)
  - `Ax-Device-Id`: MD5 hash dari fingerprint
  - `Ax-Fingerprint`: Encrypted device info
  - `Ax-Request-At`: GMT+7 timestamp tanpa colon (dikurangi 5 menit)
  - `Ax-Request-Id`: UUID v4
  - `Ax-Request-Device`: "samsung"
  - `Ax-Request-Device-Model`: "SM-N935F"
  - `Ax-Substype`: "PREPAID"
  - `User-Agent`: UA

**Validasi:**
- Nomor telepon harus dimulai dengan "628" dan maksimal 14 digit
- OTP harus 6 digit

**Signature Generation:**
- Timestamp untuk signature: GMT+7 tanpa colon (tidak dikurangi)
- Timestamp untuk header Ax-Request-At: GMT+7 tanpa colon (dikurangi 5 menit)
- Preimage: `{timestamp}password{contactType}{contact}{code}openid`
- Key: AX_API_SIG_KEY (18b4d589826af50241177961590e6693)
- Algorithm: HMAC-SHA256
- Output: Base64 encoded

---

### Perbaikan Lainnya (Partial)

#### 6. MESunsetRepository.kt - Update Get Profile dan Get Balance

**Perubahan:**
- Menggunakan payload yang benar sesuai Python CLI
- Generate X-Signature dengan `makeXSignature()`
- Menambahkan headers yang diperlukan (X-Request-At, X-Request-Id, dll)

**Catatan:**
- Implementasi masih partial, perlu menambahkan endpoint di ApiService
- Endpoint path sudah disesuaikan dengan Python CLI

---

## Referensi Python CLI

Semua perubahan di atas mengikuti implementasi dari:
- `app/client/ciam.py` - Untuk logika OTP dan autentikasi
- `app/client/encrypt.py` - Untuk signature generation dan timestamp
- `app/service/crypto_helper.py` - Untuk enkripsi dan dekripsi

## Testing yang Diperlukan

1. ✅ Request OTP dengan nomor telepon valid (628...)
2. ✅ Submit OTP dengan kode 6 digit
3. ⏳ Refresh token dengan subscriber_id
4. ⏳ Extend session ketika token expired
5. ⏳ Get profile setelah login
6. ⏳ Get balance setelah login

## Known Issues

1. Endpoint untuk Get Profile, Get Balance, dan API lainnya masih perlu ditambahkan di ApiService
2. Implementasi refresh token dan extend session belum lengkap
3. Perlu testing dengan nomor telepon dan OTP yang valid

## Next Steps

1. Menambahkan endpoint lengkap di ApiService untuk semua API
2. Implementasi refresh token logic
3. Implementasi extend session logic
4. Testing end-to-end dengan nomor telepon valid
5. Update UI untuk menampilkan error message yang lebih informatif
