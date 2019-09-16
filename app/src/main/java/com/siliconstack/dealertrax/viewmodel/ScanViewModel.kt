package com.siliconstack.dealertrax.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.model.BaseApiResponse
import com.siliconstack.dealertrax.model.Resource
import com.siliconstack.dealertrax.repository.HomeRepository
import com.siliconstack.dealertrax.model.OCRAuthRequest
import com.siliconstack.dealertrax.model.OCRModel
import com.siliconstack.dealertrax.model.OCRRequest
import javax.inject.Inject


class ScanViewModel @Inject constructor (application: AppApplication): AndroidViewModel(application) {

    @Inject
    lateinit var  homeRepository: HomeRepository
    lateinit var ocrModel: OCRModel

    init {
    }


    fun getVin(ocrRequest: OCRRequest, token:String) : LiveData<Resource<BaseApiResponse>> {
        return homeRepository.getVin(ocrRequest,token)
    }
    fun getRego(ocrRequest: OCRRequest, token:String) : LiveData<Resource<BaseApiResponse>> {
        return homeRepository.getRego(ocrRequest,token)
    }


    fun authenOCRService(ocrAuthRequest: OCRAuthRequest) : LiveData<Resource<BaseApiResponse>> {
        return homeRepository.getOCRAuth(ocrAuthRequest)
    }

}