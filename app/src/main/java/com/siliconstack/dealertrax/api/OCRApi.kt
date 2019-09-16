package com.siliconstack.dealertrax.api


import com.google.gson.JsonElement
import com.siliconstack.dealertrax.BuildConfig
import com.siliconstack.dealertrax.model.OCRAuthRequest
import com.siliconstack.dealertrax.model.OCRAuthenResponse
import com.siliconstack.dealertrax.model.OCRRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OCRApi {
//
//    @POST("Vins")
//    fun getVin(@Body ocrRequest: OCRRequest): Call<OCRModel>
//    @POST("Regos")
//    fun getRego(@Body ocrRequest: OCRRequest): Call<OCRModel>

    @POST("Vins")
    fun getVin(@Header("Authorization") token :String, @Body ocrRequest: OCRRequest): Call<JsonElement>
    @POST("Regos")
    fun getRego(@Header("Authorization") token :String, @Body ocrRequest: OCRRequest): Call<JsonElement>


    @POST(BuildConfig.SERVER_OCR_AUTH_URL+"Authorise")
    fun authenOCR(@Body ocrAuthRequest: OCRAuthRequest): Call<OCRAuthenResponse>

}
