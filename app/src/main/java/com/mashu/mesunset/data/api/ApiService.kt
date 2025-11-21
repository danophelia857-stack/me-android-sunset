package com.mashu.mesunset.data.api

import com.mashu.mesunset.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("api/v1/auth/otp/request")
    suspend fun requestOtp(
        @Header("Authorization") basicAuth: String,
        @Header("User-Agent") userAgent: String,
        @Body request: OtpRequest
    ): Response<OtpResponse>
    
    @POST("api/v1/auth/login")
    suspend fun login(
        @Header("Authorization") basicAuth: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Body request: LoginRequest
    ): Response<LoginResponse>
    
    @POST("api/v8/balance")
    suspend fun getBalance(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/tiering/info")
    suspend fun getTieringInfo(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/packages/my")
    suspend fun getMyPackages(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/packages/detail")
    suspend fun getPackageDetail(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/packages/family")
    suspend fun getPackagesByFamily(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/purchase")
    suspend fun purchasePackage(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/transactions/history")
    suspend fun getTransactionHistory(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/famplan/info")
    suspend fun getFamilyPlanInfo(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/circle/info")
    suspend fun getCircleInfo(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
}
