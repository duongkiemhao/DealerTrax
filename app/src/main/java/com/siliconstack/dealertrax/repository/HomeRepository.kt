package com.siliconstack.dealertrax.repository


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.siliconstack.dealertrax.api.OCRApi
import com.siliconstack.dealertrax.api.TeleserviceApi
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.model.*
import com.siliconstack.dealertrax.model.OCRAuthRequest
import com.siliconstack.dealertrax.model.OCRAuthenResponse
import com.siliconstack.dealertrax.model.OCRRequest
import io.reactivex.Observable


class HomeRepository (val teleserviceApi: TeleserviceApi,val OCRApi: OCRApi) : BaseRepository() {

    fun getLocations(): Observable<List<Any>> {
        return teleserviceApi.getLocations(Config.AUTHEN_KEY)
    }

    fun getFloors(): Observable<List<Any>> {
        return teleserviceApi.getFloors(Config.AUTHEN_KEY)
    }
    fun getOperators(): Observable<List<Any>> {
        return teleserviceApi.getOperators(Config.AUTHEN_KEY)
    }
    fun getStockChecks(): Observable<List<Any>> {
        return teleserviceApi.getStockChecks(Config.AUTHEN_KEY)
    }

    fun postStockChecks(mainModel: MainModel): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<List<MainModel>>>()
        teleserviceApi.postStockCheck(Config.AUTHEN_KEY,mainModel).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<List<MainModel>>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }

    fun postFloor(floorModel: FloorModel): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<List<FloorModel>>>()
        teleserviceApi.postFloor(Config.AUTHEN_KEY,floorModel).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<List<FloorModel>>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }

    fun postLocation(locationModel: LocationModel): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<List<LocationModel>>>()
        teleserviceApi.postLocation(Config.AUTHEN_KEY,locationModel).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<List<LocationModel>>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }

    fun postOperator(operatorModel: OperatorModel): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<List<OperatorModel>>>()
        teleserviceApi.postOperator(Config.AUTHEN_KEY,operatorModel).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<List<OperatorModel>>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }


    fun getVin(ocrRequest: OCRRequest, token:String): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<JsonElement>>()
        OCRApi.getVin("Bearer $token",ocrRequest).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<JsonElement>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }

    fun getRego(ocrRequest: OCRRequest, token:String): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<JsonElement>>()
        OCRApi.getRego("Bearer $token",ocrRequest).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<JsonElement>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }

    fun getOCRAuth(ocrAuthRequest: OCRAuthRequest): LiveData<Resource<BaseApiResponse>> {
        var data = MutableLiveData<Resource<OCRAuthenResponse>>()
        OCRApi.authenOCR(ocrAuthRequest).enqueue(object : BaseRepository.Companion.MyRetrofitCallback<OCRAuthenResponse>(data) {})
        return data as MutableLiveData<Resource<BaseApiResponse>>
    }

}
