package com.mashu.mesunset.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val number: String,
    val subscriberId: String,
    val subscriptionType: String,
    val tokens: Tokens
)

@Serializable
data class Tokens(
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class Balance(
    val remaining: Long,
    val expiredAt: Long
)

@Serializable
data class TieringInfo(
    val tier: Int,
    val currentPoint: Int
)

@Serializable
data class Package(
    val optionCode: String,
    val familyCode: String,
    val name: String,
    val description: String,
    val price: Long,
    val quota: String,
    val validity: String
)

@Serializable
data class Transaction(
    val id: String,
    val date: Long,
    val packageName: String,
    val amount: Long,
    val status: String
)

@Serializable
data class LoginRequest(
    val contact: String,
    val contactType: String,
    val code: String
)

@Serializable
data class LoginResponse(
    @SerializedName("id_token") val idToken: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("subscriber_id") val subscriberId: String,
    @SerializedName("subscription_type") val subscriptionType: String
)

@Serializable
data class EncryptedRequest(
    val xdata: String,
    val xtime: Long
)

@Serializable
data class EncryptedResponse(
    val xdata: String,
    val xtime: Long
)

@Serializable
data class PurchaseRequest(
    val packageCode: String,
    val paymentMethod: String,
    val tokenPayment: String
)

@Serializable
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

@Serializable
data class FamilyPlan(
    val organizerId: String,
    val members: List<String>,
    val quota: String
)

@Serializable
data class Circle(
    val circleId: String,
    val members: List<String>,
    val benefits: String
)

@Serializable
data class Bookmark(
    val optionCode: String,
    val packageName: String,
    val addedAt: Long
)

@Serializable
data class OtpRequest(
    val contact: String,
    @SerializedName("contact_type") val contactType: String = "MSISDN",
    @SerializedName("grant_type") val grantType: String = "password"
)

@Serializable
data class OtpResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("request_id") val requestId: String?
)
