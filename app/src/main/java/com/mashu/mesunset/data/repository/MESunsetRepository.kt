package com.mashu.mesunset.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashu.mesunset.data.Constants
import com.mashu.mesunset.data.api.RetrofitClient
import com.mashu.mesunset.data.models.*
import com.mashu.mesunset.crypto.CryptoHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MESunsetRepository(context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val ciamApiService = RetrofitClient.ciamApiService
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // Active user management
    fun getActiveUser(): User? {
        val userJson = prefs.getString(Constants.KEY_ACTIVE_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun saveActiveUser(user: User) {
        prefs.edit().putString(Constants.KEY_ACTIVE_USER, gson.toJson(user)).apply()
    }
    
    fun clearActiveUser() {
        prefs.edit().remove(Constants.KEY_ACTIVE_USER).apply()
    }
    
    // Users list management
    fun getAllUsers(): List<User> {
        val usersJson = prefs.getString(Constants.KEY_USERS_LIST, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(usersJson, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun addUser(user: User) {
        val users = getAllUsers().toMutableList()
        users.removeAll { it.number == user.number }
        users.add(user)
        prefs.edit().putString(Constants.KEY_USERS_LIST, gson.toJson(users)).apply()
    }
    
    fun removeUser(number: String) {
        val users = getAllUsers().toMutableList()
        users.removeAll { it.number == number }
        prefs.edit().putString(Constants.KEY_USERS_LIST, gson.toJson(users)).apply()
    }
    
    // Bookmarks management
    fun getBookmarks(): List<Bookmark> {
        val bookmarksJson = prefs.getString(Constants.KEY_BOOKMARKS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Bookmark>>() {}.type
            gson.fromJson(bookmarksJson, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    // Request OTP
    suspend fun requestOtp(msisdn: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Validate phone number
            if (!msisdn.startsWith("628") || msisdn.length > 14) {
                return@withContext Result.failure(Exception("Nomor telepon tidak valid. Harus dimulai dengan 628 dan maksimal 14 digit."))
            }
            
            // Generate device headers
            val axDeviceId = CryptoHelper.generateAxDeviceId(msisdn)
            val axFingerprint = CryptoHelper.generateAxFingerprint(msisdn)
            val axRequestAt = CryptoHelper.generateJavaLikeTimestamp()
            val axRequestId = CryptoHelper.generateUuid()
            
            val response = ciamApiService.requestOtp(
                authorization = "Basic ${Constants.BASIC_AUTH}",
                axDeviceId = axDeviceId,
                axFingerprint = axFingerprint,
                axRequestAt = axRequestAt,
                axRequestDevice = "samsung",
                axRequestDeviceModel = "SM-N935F",
                axRequestId = axRequestId,
                axSubstype = "PREPAID",
                userAgent = Constants.UA,
                contact = msisdn,
                contactType = "SMS",
                alternateContact = "false"
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.subscriberId != null) {
                    // Save subscriber ID for later use
                    prefs.edit().putString("subscriber_id_$msisdn", body.subscriberId).apply()
                    Result.success("OTP berhasil dikirim ke $msisdn")
                } else {
                    val message = body?.error ?: body?.errorDescription ?: "Request OTP gagal"
                    Result.failure(Exception(message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val message = when (e) {
                is java.net.UnknownHostException -> "Tidak ada koneksi internet"
                is java.net.SocketTimeoutException -> "Request timeout, coba lagi"
                is com.google.gson.JsonSyntaxException -> "Error parsing response dari server: ${e.message}"
                else -> "Error: ${e.message ?: e.javaClass.simpleName}"
            }
            Result.failure(Exception(message))
        }
    }
    
    // Login
    suspend fun login(msisdn: String, otp: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Validate phone number
            if (!msisdn.startsWith("628") || msisdn.length > 14) {
                return@withContext Result.failure(Exception("Nomor telepon tidak valid. Harus dimulai dengan 628 dan maksimal 14 digit."))
            }
            
            // Validate OTP
            if (otp.length != 6) {
                return@withContext Result.failure(Exception("Kode OTP harus 6 digit"))
            }
            
            // Generate device headers
            val axDeviceId = CryptoHelper.generateAxDeviceId(msisdn)
            val axFingerprint = CryptoHelper.generateAxFingerprint(msisdn)
            val axRequestAt = CryptoHelper.generateGmt7Timestamp(subtractMinutes = 5)
            val axRequestId = CryptoHelper.generateUuid()
            
            // Generate signature for submit OTP
            val tsForSign = CryptoHelper.generateGmt7Timestamp(subtractMinutes = 0)
            val axApiSignature = CryptoHelper.makeAxApiSignature(
                tsForSign = tsForSign,
                contact = msisdn,
                code = otp,
                contactType = "SMS"
            )
            
            // Call CIAM submit OTP API
            val response = ciamApiService.submitOtp(
                authorization = "Basic ${Constants.BASIC_AUTH}",
                axApiSignature = axApiSignature,
                axDeviceId = axDeviceId,
                axFingerprint = axFingerprint,
                axRequestAt = axRequestAt,
                axRequestDevice = "samsung",
                axRequestDeviceModel = "SM-N935F",
                axRequestId = axRequestId,
                axSubstype = "PREPAID",
                userAgent = Constants.UA,
                contactType = "SMS",
                code = otp,
                grantType = "password",
                contact = msisdn,
                scope = "openid"
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.accessToken != null && body.refreshToken != null && body.idToken != null) {
                    // Get subscriber ID from saved data or from profile
                    val subscriberId = prefs.getString("subscriber_id_$msisdn", "") ?: ""
                    
                    // Create user object
                    val user = User(
                        number = msisdn,
                        subscriberId = subscriberId,
                        subscriptionType = "PREPAID",
                        tokens = Tokens(
                            idToken = body.idToken,
                            accessToken = body.accessToken,
                            refreshToken = body.refreshToken
                        )
                    )
                    
                    // Save user
                    saveActiveUser(user)
                    addUser(user)
                    
                    Result.success(user)
                } else {
                    val message = body?.error ?: body?.errorDescription ?: "Login gagal"
                    Result.failure(Exception(message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val message = when (e) {
                is java.net.UnknownHostException -> "Tidak ada koneksi internet"
                is java.net.SocketTimeoutException -> "Request timeout, coba lagi"
                is com.google.gson.JsonSyntaxException -> "Error parsing response: ${e.message}"
                else -> "Login error: ${e.message ?: e.javaClass.simpleName}"
            }
            Result.failure(Exception(message))
        }
    }
    
    // Get balance
    suspend fun getBalance(user: User): Result<Balance> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val tsForSign = timestamp.toString()
            
            val signature = CryptoHelper.generateXApiSignature(tsForSign)
            
            val response = apiService.getBalance(
                authorization = "Bearer ${user.tokens.accessToken}",
                userAgent = Constants.UA,
                xApiKey = Constants.AX_FP_KEY,
                xApiSignature = signature,
                xApiTimestamp = tsForSign
            )
            
            if (response.isSuccessful) {
                val encryptedResponse = response.body()
                if (encryptedResponse != null) {
                    val decryptedJson = CryptoHelper.decryptXData(
                        encryptedResponse.xdata,
                        encryptedResponse.xtime
                    )
                    val balance = gson.fromJson(decryptedJson, Balance::class.java)
                    Result.success(balance)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Get balance error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }
    
    // Get packages
    suspend fun getPackages(user: User): Result<List<Package>> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val tsForSign = timestamp.toString()
            
            val signature = CryptoHelper.generateXApiSignature(tsForSign)
            
            val response = apiService.getPackages(
                authorization = "Bearer ${user.tokens.accessToken}",
                userAgent = Constants.UA,
                xApiKey = Constants.AX_FP_KEY,
                xApiSignature = signature,
                xApiTimestamp = tsForSign
            )
            
            if (response.isSuccessful) {
                val encryptedResponse = response.body()
                if (encryptedResponse != null) {
                    val decryptedJson = CryptoHelper.decryptXData(
                        encryptedResponse.xdata,
                        encryptedResponse.xtime
                    )
                    val type = object : TypeToken<List<Package>>() {}.type
                    val packages: List<Package> = gson.fromJson(decryptedJson, type)
                    Result.success(packages)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Get packages error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }
    
    // Get tiering info
    suspend fun getTieringInfo(user: User): Result<TieringInfo> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val tsForSign = timestamp.toString()
            
            val signature = CryptoHelper.generateXApiSignature(tsForSign)
            
            val response = apiService.getTieringInfo(
                bearerToken = "Bearer ${user.tokens.accessToken}",
                signature = signature,
                userAgent = Constants.UA,
                apiKey = Constants.AX_FP_KEY,
                encryptedRequest = EncryptedRequest("", timestamp)
            )
            
            if (response.isSuccessful) {
                val encryptedResponse = response.body()
                if (encryptedResponse != null) {
                    val decryptedJson = CryptoHelper.decryptXData(
                        encryptedResponse.xdata,
                        encryptedResponse.xtime
                    )
                    val tieringInfo = gson.fromJson(decryptedJson, TieringInfo::class.java)
                    Result.success(tieringInfo)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Get tiering info error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }
}
