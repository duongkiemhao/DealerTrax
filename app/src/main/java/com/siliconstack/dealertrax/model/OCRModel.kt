package com.siliconstack.stockcheck.model

import android.databinding.BaseObservable
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class OCRModel(
        @SerializedName("Dob")
        var dob: String?,
        @SerializedName("DriverLicenceExpiryDate")
        var driverLicenceExpiryDate: String?,
        @SerializedName("DriverLicenceNumber")
        var driverLicenceNumber: String?,
        @SerializedName("FirstName")
        var firstName: String?,
        @SerializedName("LastName")
        var lastName: String?,
        @SerializedName("ParsedContent")
        var parsedContent: String?,
        @SerializedName("PostCode")
        var postCode: String?,
        @SerializedName("State")
        var state: String?,
        @SerializedName("StreetLine1")
        var streetLine1: String?,
        @SerializedName("Suburb")
        var suburb: String?,
        @SerializedName("Vin")
                var vin: String?,
                @SerializedName("Rego")
                var rego: String?
):BaseObservable(), Parcelable {
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