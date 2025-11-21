package com.mashu.mesunset.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashu.mesunset.crypto.CryptoHelper
import com.mashu.mesunset.data.Constants
import com.mashu.mesunset.data.api.RetrofitClient
import com.mashu.mesunset.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MESunsetRepository(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val apiService = RetrofitClient.apiService
    
    // User Management
    fun getActiveUser(): User? {
        val json = prefs.getString(Constants.KEY_ACTIVE_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun setActiveUser(user: User) {
        val json = gson.toJson(user)
        prefs.edit().putString(Constants.KEY_ACTIVE_USER, json).apply()
    }
    
    fun getAllUsers(): List<User> {
        val json = prefs.getString(Constants.KEY_USERS_LIST, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addUser(user: User) {
        val users = getAllUsers().toMutableList()
        users.removeAll { it.number == user.number }
        users.add(user)
        val json = gson.toJson(users)
        prefs.edit().putString(Constants.KEY_USERS_LIST, json).apply()
        setActiveUser(user)
    }
    
    fun removeUser(number: String) {
        val users = getAllUsers().toMutableList()
        users.removeAll { it.number == number }
        val json = gson.toJson(users)
        prefs.edit().putString(Constants.KEY_USERS_LIST, json).apply()
        
        val activeUser = getActiveUser()
        if (activeUser?.number == number) {
            if (users.isNotEmpty()) {
                setActiveUser(users.first())
            } else {
                prefs.edit().remove(Constants.KEY_ACTIVE_USER).apply()
            }
        }
    }
    
    // Login
    suspend fun login(msisdn: String, otp: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val tsForSign = timestamp.toString()
            
            val signature = CryptoHelper.makeAxApiSignature(
                tsForSign = tsForSign,
                contact = msisdn,
                code = otp,
                contactType = "MSISDN"
            )
            
            val request = LoginRequest(
                contact = msisdn,
                contactType = "MSISDN",
                code = otp
            )
            
            val response = apiService.login(
                basicAuth = "Basic ${Constants.BASIC_AUTH}",
                signature = signature,
                userAgent = Constants.UA,
                request = request
            )
            
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
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
                addUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get Balance
    suspend fun getBalance(user: User): Result<Balance> = withContext(Dispatchers.IO) {
        try {
            val xtime = System.currentTimeMillis()
            val payload = "{}"
            val xdata = CryptoHelper.encryptXData(payload, xtime)
            
            val sigTimeSec = xtime / 1000
            val signature = CryptoHelper.makeXSignature(
                idToken = user.tokens.idToken,
                method = "POST",
                path = "api/v8/balance",
                sigTimeSec = sigTimeSec
            )
            
            val encryptedRequest = EncryptedRequest(xdata = xdata, xtime = xtime)
            
            val response = apiService.getBalance(
                bearerToken = "Bearer ${user.tokens.idToken}",
                signature = signature,
                userAgent = Constants.UA,
                apiKey = Constants.API_KEY,
                encryptedRequest = encryptedRequest
            )
            
            if (response.isSuccessful && response.body() != null) {
                val encryptedResponse = response.body()!!
                val decrypted = CryptoHelper.decryptXData(
                    encryptedResponse.xdata,
                    encryptedResponse.xtime
                )
                val balance = gson.fromJson(decrypted, Balance::class.java)
                Result.success(balance)
            } else {
                Result.failure(Exception("Failed to get balance: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get Tiering Info
    suspend fun getTieringInfo(user: User): Result<TieringInfo> = withContext(Dispatchers.IO) {
        try {
            val xtime = System.currentTimeMillis()
            val payload = "{}"
            val xdata = CryptoHelper.encryptXData(payload, xtime)
            
            val sigTimeSec = xtime / 1000
            val signature = CryptoHelper.makeXSignature(
                idToken = user.tokens.idToken,
                method = "POST",
                path = "api/v8/tiering/info",
                sigTimeSec = sigTimeSec
            )
            
            val encryptedRequest = EncryptedRequest(xdata = xdata, xtime = xtime)
            
            val response = apiService.getTieringInfo(
                bearerToken = "Bearer ${user.tokens.idToken}",
                signature = signature,
                userAgent = Constants.UA,
                apiKey = Constants.API_KEY,
                encryptedRequest = encryptedRequest
            )
            
            if (response.isSuccessful && response.body() != null) {
                val encryptedResponse = response.body()!!
                val decrypted = CryptoHelper.decryptXData(
                    encryptedResponse.xdata,
                    encryptedResponse.xtime
                )
                val tieringInfo = gson.fromJson(decrypted, TieringInfo::class.java)
                Result.success(tieringInfo)
            } else {
                Result.failure(Exception("Failed to get tiering info: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Bookmarks
    fun getBookmarks(): List<Bookmark> {
        val json = prefs.getString(Constants.KEY_BOOKMARKS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Bookmark>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addBookmark(bookmark: Bookmark) {
        val bookmarks = getBookmarks().toMutableList()
        bookmarks.removeAll { it.optionCode == bookmark.optionCode }
        bookmarks.add(bookmark)
        val json = gson.toJson(bookmarks)
        prefs.edit().putString(Constants.KEY_BOOKMARKS, json).apply()
    }
    
    fun removeBookmark(optionCode: String) {
        val bookmarks = getBookmarks().toMutableList()
        bookmarks.removeAll { it.optionCode == optionCode }
        val json = gson.toJson(bookmarks)
        prefs.edit().putString(Constants.KEY_BOOKMARKS, json).apply()
    }
}
