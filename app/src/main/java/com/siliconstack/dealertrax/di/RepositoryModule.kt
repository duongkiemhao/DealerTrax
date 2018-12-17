package com.siliconstack.dealertrax.di

import com.siliconstack.dealertrax.api.OCRApi
import com.siliconstack.dealertrax.api.TeleserviceApi
import com.siliconstack.dealertrax.repository.HomeRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Created by bsobat on 26/05/17.
 */
@Module
class RepositoryModule{

    @Provides
    @Singleton
    fun provideAppRepository(teleserviceApi: TeleserviceApi,OCRApi: OCRApi): HomeRepository {
        return HomeRepository(teleserviceApi,OCRApi)
    }


}