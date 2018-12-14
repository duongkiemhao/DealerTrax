package com.siliconstack.dealertrax.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity()
data class MainModel (
        @PrimaryKey(autoGenerate = true)
        @SerializedName("Id")
        var id:Int,
        @SerializedName("ScanText")
        var scanText:String?,
        @SerializedName("Timestamp")
        var timestamp:Long,

        @SerializedName("LocationId")
        var locationID:Int?=null,
        @SerializedName("FloorId")
        var floorID:Int?=null,
        @SerializedName("OperatorId")
        var operatorID:Int?=null,
        @SerializedName("BayNumber")
        var bayNumber:String?=null,
        @SerializedName("ScanTextTypeId")
        var scanTextTypeId:Int?=null,
        @SerializedName("Longitude")
        var longitude:Double?=null,
        @SerializedName("Latitude")
        var latitude:Double?=null

        )