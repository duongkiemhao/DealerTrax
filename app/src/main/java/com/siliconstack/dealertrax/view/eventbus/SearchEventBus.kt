package com.siliconstack.dealertrax.view.eventbus

import android.graphics.Bitmap

class SearchEventBus{
    var reload: Boolean? =null

    constructor(reload:Boolean?){
        this.reload=reload
    }
}