package com.siliconstack.dealertrax.model

import android.os.Parcel
import android.os.Parcelable
import kotlin.Comparator


open class MainDTO() : Comparator<MainDTO>, Parcelable {
    override fun compare(o1: MainDTO?, o2: MainDTO?): Int {
        if(!isDesc)
            return ((o1?.timestamp?:0)-(o2?.timestamp?:0)).toInt()
        return return ((o2?.timestamp?:0)-(o1?.timestamp?:0)).toInt()
    }

    var isDesc:Boolean = false

    constructor(isDesc:Boolean) : this() {
        this.isDesc=isDesc
    }


    var id:Int? = null
        var scanText:String?=null
        var timestamp:Long?=null
        var type:Int?=null


        var locationID:Int?=null
        var locationName:String?=null
        var floorID:Int?=null
        var floorName:String?=null
        var operatorID:Int?=null
        var operatorName:String?=null
        var bayNumber:String?=null
        var dateString:String?=null

        var isSelected:Boolean=false
        var compareTime:String=""
        var compareTimeFullStr:String=""
        var image:String?=null
        var longitude:Double?=null
        var latitude:Double?=null

    constructor(parcel: Parcel) : this() {
        isDesc = parcel.readByte() != 0.toByte()
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        scanText = parcel.readString()
        timestamp = parcel.readValue(Long::class.java.classLoader) as? Long
        type = parcel.readValue(Int::class.java.classLoader) as? Int
        locationID = parcel.readValue(Int::class.java.classLoader) as? Int
        locationName = parcel.readString()
        floorID = parcel.readValue(Int::class.java.classLoader) as? Int
        floorName = parcel.readString()
        operatorID = parcel.readValue(Int::class.java.classLoader) as? Int
        operatorName = parcel.readString()
        bayNumber = parcel.readString()
        dateString = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
        compareTime = parcel.readString()
        compareTimeFullStr = parcel.readString()
        image = parcel.readString()
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isDesc) 1 else 0)
        parcel.writeValue(id)
        parcel.writeString(scanText)
        parcel.writeValue(timestamp)
        parcel.writeValue(type)
        parcel.writeValue(locationID)
        parcel.writeString(locationName)
        parcel.writeValue(floorID)
        parcel.writeString(floorName)
        parcel.writeValue(operatorID)
        parcel.writeString(operatorName)
        parcel.writeString(bayNumber)
        parcel.writeString(dateString)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeString(compareTime)
        parcel.writeString(compareTimeFullStr)
        parcel.writeString(image)
        parcel.writeValue(longitude)
        parcel.writeValue(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainDTO> {
        override fun createFromParcel(parcel: Parcel): MainDTO {
            return MainDTO(parcel)
        }

        override fun newArray(size: Int): Array<MainDTO?> {
            return arrayOfNulls(size)
        }
    }


}