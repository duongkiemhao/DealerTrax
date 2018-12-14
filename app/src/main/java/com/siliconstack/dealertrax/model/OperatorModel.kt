package com.siliconstack.dealertrax.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity()
data class OperatorModel (
        @SerializedName("Name")
        var name:String,
        @PrimaryKey(autoGenerate = true)
        @SerializedName("Id")
        var id:Int
){
        override fun toString(): String {
                return name
        }
}