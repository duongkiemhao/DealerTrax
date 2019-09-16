package com.siliconstack.dealertrax.model


import android.databinding.BaseObservable
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class OCRModel(
        @SerializedName("dob")
        var dob: String?="",
        @SerializedName("driverLicenceExpiryDate")
        var driverLicenceExpiryDate: String?="",
        @SerializedName("driverLicenceNumber")
        var driverLicenceNumber: String?="",
        @SerializedName("firstName")
        var firstName: String?="",
        @SerializedName("lastName")
        var lastName: String?="",
        @SerializedName("parsedContent")
        var parsedContent: String?,
        @SerializedName("postCode")
        var postCode: String?="",
        @SerializedName("state")
        var state: String?="",
        @SerializedName("streetLine1")
        var streetLine1: String?="",
        @SerializedName("suburb")
        var suburb: String?="",
        @SerializedName("vin")
        var vin: String?,
        @SerializedName("rego")
        var rego: String?,
        @SerializedName("version")
        var version: String?
) : BaseObservable(), Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(dob)
                parcel.writeString(driverLicenceExpiryDate)
                parcel.writeString(driverLicenceNumber)
                parcel.writeString(firstName)
                parcel.writeString(lastName)
                parcel.writeString(parsedContent)
                parcel.writeString(postCode)
                parcel.writeString(state)
                parcel.writeString(streetLine1)
                parcel.writeString(suburb)
                parcel.writeString(vin)
                parcel.writeString(rego)
                parcel.writeString(version)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<OCRModel> {
                override fun createFromParcel(parcel: Parcel): OCRModel {
                        return OCRModel(parcel)
                }

                override fun newArray(size: Int): Array<OCRModel?> {
                        return arrayOfNulls(size)
                }
        }
}

data class OCRRequest(val countryCode: String, var imageData:String)
data class OCRAuthRequest(val systemCode: String, val customer:String, val reference:String, val apiKey:String)
data class OCRAuthenResponse(val data: JsonElement, val code:Int, val message:String)
