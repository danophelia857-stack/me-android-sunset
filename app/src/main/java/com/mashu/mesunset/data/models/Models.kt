package com.mashu.mesunset.data.models

import com.google.gson.annotations.SerializedName

// User models
data class User(
    val number: String,
    val subscriberId: String,
    val subscriptionType: String,
    val tokens: Tokens
)

data class Tokens(
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
)

data class Balance(
    val remaining: Long,
    val expiredAt: Long
)

data class TieringInfo(
    val tier: Int,
    val currentPoint: Int
)

data class Package(
    val optionCode: String,
    val familyCode: String,
    val name: String,
    val description: String,
    val price: Long,
    val quota: String,
    val validity: String
)

data class Transaction(
    val id: String,
    val date: Long,
    val packageName: String,
    val amount: Long,
    val status: String
)

// Request/Response models
data class LoginRequest(
    val contact: String,
    @SerializedName("contact_type")
    val contactType: String,
    val code: String
)

data class LoginResponse(
    @SerializedName("id_token")
    val idToken: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("subscriber_id")
    val subscriberId: String,
    @SerializedName("subscription_type")
    val subscriptionType: String
)

data class OtpRequest(
    val contact: String,
    @SerializedName("contact_type")
    val contactType: String = "MSISDN",
    @SerializedName("grant_type")
    val grantType: String = "password"
)

data class OtpResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("request_id")
    val requestId: String? = null
)

data class EncryptedRequest(
    val xdata: String,
    val xtime: Long
)

data class EncryptedResponse(
    val xdata: String,
    val xtime: Long
)

data class PurchaseRequest(
    val packageCode: String,
    val paymentMethod: String,
    val tokenPayment: String
)

data class PurchaseResponse(
    val success: Boolean,
    val message: String,
    val transactionId: String?
)

data class Profile(
    val number: String,
    val subscriberId: String,
    val subscriptionType: String,
    val balance: Long,
    val balanceExpiredAt: Long,
    val pointInfo: String
)

data class FamilyPlan(
    val organizerId: String,
    val members: List<String>,
    val quota: String
)

data class Circle(
    val circleId: String,
    val members: List<String>,
    val benefits: String
)

data class Bookmark(
    val optionCode: String,
    val packageName: String,
    val addedAt: Long
)

// CIAM API models
data class CiamOtpResponse(
    @SerializedName("subscriber_id")
    val subscriberId: String? = null,
    val error: String? = null,
    @SerializedName("error_description")
    val errorDescription: String? = null
)

data class CiamTokenResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("id_token")
    val idToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    @SerializedName("expires_in")
    val expiresIn: Int? = null,
    val error: String? = null,
    @SerializedName("error_description")
    val errorDescription: String? = null
)

data class CiamExtendSessionResponse(
    val data: CiamExtendSessionData? = null,
    val error: String? = null,
    @SerializedName("error_description")
    val errorDescription: String? = null
)

data class CiamExtendSessionData(
    @SerializedName("exchange_code")
    val exchangeCode: String
)
