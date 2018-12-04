package com.siliconstack.dealertrax

import com.marcinmoskala.kotlinpreferences.PreferenceHolder

import com.siliconstack.dealertrax.model.FilterDialogModel


object PreferenceSetting : PreferenceHolder() {


    var locationSetting: FilterDialogModel? by bindToPreferenceFieldNullable("locationSetting")
    var floorSetting: FilterDialogModel? by bindToPreferenceFieldNullable("floorSetting")
    var nameSetting: FilterDialogModel? by bindToPreferenceFieldNullable("nameSetting")

}
