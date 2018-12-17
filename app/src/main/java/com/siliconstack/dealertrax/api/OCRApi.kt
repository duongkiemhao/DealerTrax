package com.siliconstack.dealertrax.api


import com.siliconstack.stockcheck.model.OCRModel
import com.siliconstack.stockcheck.model.OCRRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OCRApi {

    @POST("Vins")
    fun getVin(@Body ocrRequest: OCRRequest): Call<OCRModel>
    @POST("Regos")
    fun getRego(@Body ocrRequest: OCRRequest): Call<OCRModel>
}
