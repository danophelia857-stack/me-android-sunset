package com.mashu.mesunset.data.api

import com.mashu.mesunset.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface CiamApiService {
    
    @GET("realms/xl-ciam/auth/otp")
    suspend fun requestOtp(
        @Header("Authorization") authorization: String,
        @Header("Ax-Device-Id") axDeviceId: String,
        @Header("Ax-Fingerprint") axFingerprint: String,
        @Header("Ax-Request-At") axRequestAt: String,
        @Header("Ax-Request-Device") axRequestDevice: String,
        @Header("Ax-Request-Device-Model") axRequestDeviceModel: String,
        @Header("Ax-Request-Id") axRequestId: String,
        @Header("Ax-Substype") axSubstype: String,
        @Header("User-Agent") userAgent: String,
        @Query("contact") contact: String,
        @Query("contactType") contactType: String,
        @Query("alternateContact") alternateContact: String
    ): Response<CiamOtpResponse>
    
    @POST("realms/xl-ciam/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun submitOtp(
        @Header("Authorization") authorization: String,
        @Header("Ax-Api-Signature") axApiSignature: String,
        @Header("Ax-Device-Id") axDeviceId: String,
        @Header("Ax-Fingerprint") axFingerprint: String,
        @Header("Ax-Request-At") axRequestAt: String,
        @Header("Ax-Request-Device") axRequestDevice: String,
        @Header("Ax-Request-Device-Model") axRequestDeviceModel: String,
        @Header("Ax-Request-Id") axRequestId: String,
        @Header("Ax-Substype") axSubstype: String,
        @Header("User-Agent") userAgent: String,
        @Field("contactType") contactType: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String,
        @Field("contact") contact: String,
        @Field("scope") scope: String
    ): Response<CiamTokenResponse>
    
    @GET("realms/xl-ciam/auth/extend-session")
    suspend fun extendSession(
        @Header("Authorization") authorization: String,
        @Header("Ax-Device-Id") axDeviceId: String,
        @Header("Ax-Fingerprint") axFingerprint: String,
        @Header("Ax-Request-At") axRequestAt: String,
        @Header("Ax-Request-Device") axRequestDevice: String,
        @Header("Ax-Request-Device-Model") axRequestDeviceModel: String,
        @Header("Ax-Request-Id") axRequestId: String,
        @Header("Ax-Substype") axSubstype: String,
        @Header("User-Agent") userAgent: String,
        @Query("contact") contact: String,
        @Query("contactType") contactType: String
    ): Response<CiamExtendSessionResponse>
    
    @POST("realms/xl-ciam/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Header("Authorization") authorization: String,
        @Header("Ax-Device-Id") axDeviceId: String,
        @Header("Ax-Fingerprint") axFingerprint: String,
        @Header("Ax-Request-At") axRequestAt: String,
        @Header("Ax-Request-Device") axRequestDevice: String,
        @Header("Ax-Request-Device-Model") axRequestDeviceModel: String,
        @Header("Ax-Request-Id") axRequestId: String,
        @Header("Ax-Substype") axSubstype: String,
        @Header("User-Agent") userAgent: String,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String
    ): Response<CiamTokenResponse>
}

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
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") xApiKey: String,
        @Header("x-api-signature") xApiSignature: String,
        @Header("x-api-timestamp") xApiTimestamp: String,
        @Body request: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/balance")
    suspend fun getBalance(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") xApiKey: String,
        @Header("x-api-signature") xApiSignature: String,
        @Header("x-api-timestamp") xApiTimestamp: String
    ): Response<EncryptedResponse>
    
    @POST("api/v8/tiering/info")
    suspend fun getTieringInfo(
        @Header("Authorization") bearerToken: String,
        @Header("X-Signature") signature: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") apiKey: String,
        @Body encryptedRequest: EncryptedRequest
    ): Response<EncryptedResponse>
    
    @POST("api/v8/packages/hot")
    suspend fun getPackages(
        @Header("Authorization") authorization: String,
        @Header("User-Agent") userAgent: String,
        @Header("x-api-key") xApiKey: String,
        @Header("x-api-signature") xApiSignature: String,
        @Header("x-api-timestamp") xApiTimestamp: String
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
