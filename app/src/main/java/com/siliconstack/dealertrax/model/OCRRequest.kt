package com.siliconstack.stockcheck.model

import com.google.gson.annotations.SerializedName

class OCRRequest{
    @SerializedName("ImageData")
    var imageData:String?=null
    @SerializedName("ScanType")
    var scanType:Int?=null
}