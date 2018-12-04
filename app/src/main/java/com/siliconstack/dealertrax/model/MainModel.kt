package com.siliconstack.dealertrax.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity()
data class MainModel (
        @PrimaryKey(autoGenerate = true)
        var id:Int,
        var scanText:String,
        var timestamp:Long,
        var type:Int,


        var locationID:Int?=null,
        var floorID:Int?=null,
        var operatorID:Int?=null,

        var bayNumber:String?=null,
                var lat:Double?=null,
        var lng:Double?=null,
        var image:String?=null

){
        var dateString:String?=null




}