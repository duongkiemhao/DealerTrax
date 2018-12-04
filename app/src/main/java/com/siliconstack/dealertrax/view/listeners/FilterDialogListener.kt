package com.siliconstack.dealertrax.view.listeners

import com.siliconstack.dealertrax.model.FilterDialogModel

interface FilterDialogListener{
    fun onSelectOk(filterDialogModel: FilterDialogModel)
}