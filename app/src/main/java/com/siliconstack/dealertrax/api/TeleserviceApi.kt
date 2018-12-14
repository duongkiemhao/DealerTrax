package com.siliconstack.dealertrax.api

import com.siliconstack.dealertrax.model.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TeleserviceApi {

    @GET("Locations")
    fun getLocations(@Header("Authorization") token :String): Observable<List<Any>>
    @GET("Floors")
    fun getFloors(@Header("Authorization") token :String): Observable<List<Any>>
    @GET("Operators")
    fun getOperators(@Header("Authorization") token :String): Observable<List<Any>>
    @GET("StockChecks")
    fun getStockChecks(@Header("Authorization") token :String): Observable<List<Any>>

    @POST("Locations")
    fun postLocation(@Header("Authorization") token :String,@Body locationModel: LocationModel): Call<List<LocationModel>>
    @POST("Floors")
    fun postFloor(@Header("Authorization") token :String,@Body floorModel: FloorModel): Call<List<FloorModel>>
    @POST("Operators")
    fun postOperator(@Header("Authorization") token :String,@Body operatorModel: OperatorModel): Call<List<OperatorModel>>
    @POST("StockChecks")
    fun postStockCheck(@Header("Authorization") token :String,@Body mainModel: MainModel): Call<List<MainModel>>
}
