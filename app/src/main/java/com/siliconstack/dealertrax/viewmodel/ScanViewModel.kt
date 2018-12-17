package com.siliconstack.dealertrax.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SimpleSQLiteQuery
import com.orhanobut.logger.Logger
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.model.BaseApiResponse
import com.siliconstack.dealertrax.model.Resource
import com.siliconstack.dealertrax.repository.HomeRepository
import com.siliconstack.stockcheck.model.OCRModel
import com.siliconstack.stockcheck.model.OCRRequest
import javax.inject.Inject


class ScanViewModel @Inject constructor (application: AppApplication): AndroidViewModel(application) {

    @Inject
    lateinit var  homeRepository: HomeRepository
    lateinit var ocrModel: OCRModel

    init {
    }


    fun getVin(ocrRequest: OCRRequest) : LiveData<Resource<BaseApiResponse>> {
        return homeRepository.getVin(ocrRequest)
    }
    fun getRego(ocrRequest: OCRRequest) : LiveData<Resource<BaseApiResponse>> {
        return homeRepository.getRego(ocrRequest)
    }

}