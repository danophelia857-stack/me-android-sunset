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
            val request = OtpRequest(
                contact = msisdn,
                contactType = "MSISDN",
                grantType = "password"
            )
            
            val response = apiService.requestOtp(
                basicAuth = "Basic ${Constants.BASIC_AUTH}",
                userAgent = Constants.UA,
                request = request
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success("OTP berhasil dikirim ke $msisdn")
                } else {
                    val message = body?.message ?: "Request OTP gagal"
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
                is com.google.gson.JsonSyntaxException -> "Error parsing response dari server"
                else -> "Error: ${e.message ?: e.javaClass.simpleName}"
            }
            Result.failure(Exception(message))
        }
    }
    
    // Login
    suspend fun login(msisdn: String, otp: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val tsForSign = timestamp.toString()
            
            // Create login request
            val loginRequest = LoginRequest(
                contact = msisdn,
                contactType = "MSISDN",
                code = otp
            )
            
            // Encrypt request
            val plaintext = gson.toJson(loginRequest)
            val xdata = CryptoHelper.encryptXData(plaintext, timestamp)
            val encryptedRequest = EncryptedRequest(xdata, timestamp)
            
            // Generate signature
            val signature = CryptoHelper.generateXApiSignature(tsForSign)
            
            // Call API
            val response = apiService.login(
                basicAuth = "Basic ${Constants.BASIC_AUTH}",
                userAgent = Constants.UA,
                xApiKey = Constants.AX_FP_KEY,
                xApiSignature = signature,
                xApiTimestamp = tsForSign,
                request = encryptedRequest
            )
            
            if (response.isSuccessful) {
                val encryptedResponse = response.body()
                if (encryptedResponse != null) {
                    // Decrypt response
                    val decryptedJson = CryptoHelper.decryptXData(
                        encryptedResponse.xdata,
                        encryptedResponse.xtime
                    )
                    val loginResponse = gson.fromJson(decryptedJson, LoginResponse::class.java)
                    
                    // Create user object
                    val user = User(
                        number = msisdn,
                        subscriberId = loginResponse.subscriberId,
                        subscriptionType = loginResponse.subscriptionType,
                        tokens = Tokens(
                            idToken = loginResponse.idToken,
                            accessToken = loginResponse.accessToken,
                            refreshToken = loginResponse.refreshToken
                        )
                    )
                    
                    // Save user
                    saveActiveUser(user)
                    addUser(user)
                    
                    Result.success(user)
                } else {
                    Result.failure(Exception("Response body is null"))
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
