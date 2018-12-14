package com.siliconstack.dealertrax.config

import com.google.android.gms.maps.GoogleMap
import com.siliconstack.dealertrax.BuildConfig

class Config{

        companion object  {

            var AUTHEN_KEY="Basic c3RvY2tjaGVjazpuc2Jtd3NpbGljb24="
            val CLOUD_VISION_API_KEY= "AIzaSyD6d3sgKi3TKw9nQ0nMe_5YmCAQtZsR8VU"
            val CLOUD_VISION_DETECT_TYPE= "DOCUMENT_TEXT_DETECTION"
            val CUSTOMER_TYPE="Customer"
            val MAX_CACHE_DIR_SIZE = 20*1000*1000L

            var DATE_TIME_PATTERN="dd/MM/yyyy HH:mm"
            var LIMIT=5000
            var MAP_ZOOM=18F
            var MAP_DEFAULT_TYPE= GoogleMap.MAP_TYPE_TERRAIN



        }

}


