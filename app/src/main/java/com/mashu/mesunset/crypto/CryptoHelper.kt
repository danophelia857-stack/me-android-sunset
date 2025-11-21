package com.mashu.mesunset.crypto

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {
    
    private const val XDATA_KEY = "5dccbf08920a5527b99e222789c34bb7"
    private const val AX_API_SIG_KEY = "18b4d589826af50241177961590e6693"
    private const val X_API_BASE_SECRET = "mU1Y4n1vBjf3M7tMnRkFU08mVyUJHed8B5En3EAniu1mXLixeuASmBmKnkyzVziOye7rG5nIekMdthensbQMcOJ6SLnrkGyfXALD7mrBC6vuWv6G01pmD3XlU5rT7Tzx"
    private const val ENCRYPTED_FIELD_KEY = "5dccbf08920a5527"
    private const val CIRCLE_MSISDN_KEY = "5dccbf08920a5527"
    private const val AX_FP_KEY = "18b4d589826af50241177961590e6693"
    
    // Cached fingerprint and device ID
    private var cachedFingerprint: String? = null
    private var cachedDeviceId: String? = null
    
    /**
     * Derive IV from xtime timestamp
     */
    private fun deriveIv(xtimeMs: Long): ByteArray {
        val sha = MessageDigest.getInstance("SHA-256")
            .digest(xtimeMs.toString().toByteArray())
        return sha.copyOf(16)
    }
    
    /**
     * Encrypt xdata with AES-CBC
     */
    fun encryptXData(plaintext: String, xtimeMs: Long): String {
        val iv = deriveIv(xtimeMs)
        val keyBytes = XDATA_KEY.toByteArray()
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyBytes, "AES"), IvParameterSpec(iv))
        
        val encrypted = cipher.doFinal(plaintext.toByteArray())
        return Base64.encodeToString(encrypted, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
    
    /**
     * Decrypt xdata with AES-CBC
     */
    fun decryptXData(xdata: String, xtimeMs: Long): String {
        val iv = deriveIv(xtimeMs)
        val keyBytes = XDATA_KEY.toByteArray()
        
        // Add padding if needed
        val paddedXdata = when (xdata.length % 4) {
            0 -> xdata
            else -> xdata + "=".repeat(4 - (xdata.length % 4))
        }
        
        val encrypted = Base64.decode(paddedXdata, Base64.URL_SAFE)
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(keyBytes, "AES"), IvParameterSpec(iv))
        
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted)
    }
    
    /**
     * Make X-Signature for API requests
     */
    fun makeXSignature(
        idToken: String,
        method: String,
        path: String,
        sigTimeSec: Long
    ): String {
        val keyStr = "$X_API_BASE_SECRET;$idToken;$method;$path;$sigTimeSec"
        val msg = "$idToken;$sigTimeSec;"
        
        return hmacSha512(keyStr, msg)
    }
    
    /**
     * Make X-Signature for payment
     */
    fun makeXSignaturePayment(
        accessToken: String,
        sigTimeSec: Long,
        packageCode: String,
        tokenPayment: String,
        paymentMethod: String,
        paymentFor: String,
        path: String
    ): String {
        val keyStr = "$X_API_BASE_SECRET;$sigTimeSec#ae-hei_9Tee6he+Ik3Gais5=;POST;$path;$sigTimeSec"
        val msg = "$accessToken;$tokenPayment;$sigTimeSec;$paymentFor;$paymentMethod;$packageCode;"
        
        return hmacSha512(keyStr, msg)
    }
    
    /**
     * Make AX API Signature
     */
    fun makeAxApiSignature(
        tsForSign: String,
        contact: String,
        code: String,
        contactType: String
    ): String {
        val preimage = "${tsForSign}password${contactType}${contact}${code}openid"
        val digest = hmacSha256Bytes(AX_API_SIG_KEY, preimage)
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }
    
    /**
     * Make X-Signature for bounty
     */
    fun makeXSignatureBounty(
        accessToken: String,
        sigTimeSec: Long,
        packageCode: String,
        tokenPayment: String
    ): String {
        val path = "api/v8/personalization/bounties-exchange"
        val keyStr = "$X_API_BASE_SECRET;$accessToken;$sigTimeSec#ae-hei_9Tee6he+Ik3Gais5=;POST;$path;$sigTimeSec"
        val msg = "$accessToken;$tokenPayment;$sigTimeSec;$packageCode;"
        
        return hmacSha512(keyStr, msg)
    }
    
    /**
     * Make X-Signature for loyalty
     */
    fun makeXSignatureLoyalty(
        sigTimeSec: Long,
        packageCode: String,
        tokenConfirmation: String,
        path: String
    ): String {
        val keyStr = "$X_API_BASE_SECRET;$sigTimeSec#ae-hei_9Tee6he+Ik3Gais5=;POST;$path;$sigTimeSec"
        val msg = "$tokenConfirmation;$sigTimeSec;$packageCode;"
        
        return hmacSha512(keyStr, msg)
    }
    
    /**
     * Encrypt Circle MSISDN
     */
    fun encryptCircleMsisdn(msisdn: String): String {
        val keyBytes = CIRCLE_MSISDN_KEY.toByteArray()
        val ivHex = generateRandomHex(8)
        val iv = ivHex.toByteArray()
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyBytes, "AES"), IvParameterSpec(iv))
        
        val encrypted = cipher.doFinal(msisdn.toByteArray())
        val ctB64 = Base64.encodeToString(encrypted, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        
        return ctB64 + ivHex
    }
    
    /**
     * Decrypt Circle MSISDN
     */
    fun decryptCircleMsisdn(encryptedMsisdnB64: String): String {
        if (encryptedMsisdnB64.length < 16) return ""
        
        val ivAscii = encryptedMsisdnB64.takeLast(16)
        val b64Part = encryptedMsisdnB64.dropLast(16)
        
        val keyBytes = ENCRYPTED_FIELD_KEY.toByteArray()
        val iv = ivAscii.toByteArray()
        
        // Add padding if needed
        val paddedB64 = when (b64Part.length % 4) {
            0 -> b64Part
            else -> b64Part + "=".repeat(4 - (b64Part.length % 4))
        }
        
        return try {
            val encrypted = Base64.decode(paddedB64, Base64.URL_SAFE)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(keyBytes, "AES"), IvParameterSpec(iv))
            
            val decrypted = cipher.doFinal(encrypted)
            String(decrypted)
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * HMAC-SHA512
     */
    private fun hmacSha512(key: String, message: String): String {
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA512"))
        val bytes = mac.doFinal(message.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * HMAC-SHA256 (returns bytes)
     */
    private fun hmacSha256Bytes(key: String, message: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA256"))
        return mac.doFinal(message.toByteArray())
    }
    
    /**
     * Generate random hex string
     */
    private fun generateRandomHex(length: Int): String {
        val bytes = ByteArray(length)
        java.security.SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Generate X-API-Signature for general API calls
     */
    fun generateXApiSignature(timestamp: String): String {
        val preimage = "${timestamp}${AX_API_SIG_KEY}"
        val digest = hmacSha256Bytes(AX_API_SIG_KEY, preimage)
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }
    
    /**
     * Build encrypted field
     */
    fun buildEncryptedField(ivHex16: String? = null, urlsafeB64: Boolean = false): String {
        val keyBytes = ENCRYPTED_FIELD_KEY.toByteArray()
        val ivHex = ivHex16 ?: generateRandomHex(8)
        val iv = ivHex.toByteArray()
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyBytes, "AES"), IvParameterSpec(iv))
        
        val encrypted = cipher.doFinal(ByteArray(16)) // Empty padded block
        
        val flags = if (urlsafeB64) {
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        } else {
            Base64.NO_WRAP or Base64.NO_PADDING
        }
        
        return Base64.encodeToString(encrypted, flags) + ivHex
    }
    
    /**
     * Build fingerprint plain text
     * Format: manufacturer|model|lang|resolution|tz_short|ip|font_scale|Android release|msisdn
     */
    private fun buildFingerprintPlain(msisdn: String = "6281398370564"): String {
        val manufacturer = "samsung" + (1000..9999).random()
        val model = "SM-N93" + (1000..9999).random()
        val lang = "en"
        val resolution = "720x1540"
        val tzShort = "GMT07:00"
        val ip = "192.169.69.69"
        val fontScale = "1.0"
        val androidRelease = "13"
        
        return "$manufacturer|$model|$lang|$resolution|$tzShort|$ip|$fontScale|Android $androidRelease|$msisdn"
    }
    
    /**
     * Generate Ax-Fingerprint
     */
    fun generateAxFingerprint(msisdn: String = "6281398370564"): String {
        if (cachedFingerprint != null) {
            return cachedFingerprint!!
        }
        
        val key = AX_FP_KEY.toByteArray()
        val iv = ByteArray(16) // All zeros
        val plaintext = buildFingerprintPlain(msisdn)
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        
        val encrypted = cipher.doFinal(plaintext.toByteArray())
        val fingerprint = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        
        cachedFingerprint = fingerprint
        return fingerprint
    }
    
    /**
     * Generate Ax-Device-Id (MD5 hash of fingerprint)
     */
    fun generateAxDeviceId(msisdn: String = "6281398370564"): String {
        if (cachedDeviceId != null) {
            return cachedDeviceId!!
        }
        
        val fingerprint = generateAxFingerprint(msisdn)
        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(fingerprint.toByteArray())
        val deviceId = digest.joinToString("") { "%02x".format(it) }
        
        cachedDeviceId = deviceId
        return deviceId
    }
    
    /**
     * Generate Java-like timestamp
     * Format: 2023-10-20T12:34:56.78+07:00
     */
    fun generateJavaLikeTimestamp(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT+7")
        
        val millis = calendar.get(Calendar.MILLISECOND)
        val ms2Digits = String.format("%02d", millis / 10)
        
        return sdf.format(calendar.time) + ".${ms2Digits}+07:00"
    }
    
    /**
     * Generate GMT+7 timestamp without colon
     * Format: 2023-10-20T12:34:56.789+0700
     */
    fun generateGmt7Timestamp(subtractMinutes: Int = 0): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
        if (subtractMinutes > 0) {
            calendar.add(Calendar.MINUTE, -subtractMinutes)
        }
        
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT+7")
        
        val millis = calendar.get(Calendar.MILLISECOND)
        val ms3Digits = String.format("%03d", millis)
        
        return sdf.format(calendar.time) + ".${ms3Digits}+0700"
    }
    
    /**
     * Generate UUID v4
     */
    fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }
}
