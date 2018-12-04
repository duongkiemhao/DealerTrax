package com.siliconstack.dealertrax.repository


import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.api.AppApi
import com.siliconstack.dealertrax.config.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class HomeRepository (val okHttpClient: OkHttpClient) : BaseRepository() {

    fun createAppApiInstance():AppApi{
        return Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(AppApplication.gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build().create(AppApi::class.java)!!
    }



}
