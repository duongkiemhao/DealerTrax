package com.siliconstack.dealertrax.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class FilterDialogModel(var name: String, var code: String, var isSelect: Boolean) : Parcelable,Cloneable{


    constructor(parcel: android.os.Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeByte(if (isSelect) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterDialogModel> {
        override fun createFromParcel(parcel: android.os.Parcel): FilterDialogModel {
            return FilterDialogModel(parcel)
        }

        override fun newArray(size: Int): Array<FilterDialogModel?> {
            return arrayOfNulls(size)
        }
    }

    override public fun clone(): FilterDialogModel {
        return super.clone() as FilterDialogModel
    }

}


data class SelectionModel(


        @SerializedName("Stores") val stores: List<Store>,
        @SerializedName("Departments") val departments: List<Department>,
        @SerializedName("Printers") val printer: List<Printer>,
        @SerializedName("Salutations") val salutation: List<Salutation>,
        @SerializedName("PrivacyCodes") val privacyCode: List<PrivacyCode>,
        @SerializedName("ContactTypes") val contactType: List<ContactType>,
        @SerializedName("TransportOptions") val transportOption: List<TransportOption>

)


data class Store(
        @SerializedName("StoreNumber") val storeNumber: String,
        @SerializedName("StoreName") val storeName: String
)

data class Department(
        @SerializedName("DepartmentCode") val departmentCode: String,
        @SerializedName("DepartmentName") val departmentName: String
)

data class Printer(
        @SerializedName("PrinterName") val printerName: String,
        @SerializedName("PrinterDesc") val printerDesc: String
)

data class Salutation(
        @SerializedName("Code") val code: String,
        @SerializedName("Description") val description: String
)

data class PrivacyCode(
        @SerializedName("Code") val code: String,
        @SerializedName("Description") val description: String
)

data class ContactType(
        @SerializedName("Code") val code: String,
        @SerializedName("Description") val description: String
)

data class TransportOption(
        @SerializedName("Code") val code: String,
        @SerializedName("Description") val description: String
)

