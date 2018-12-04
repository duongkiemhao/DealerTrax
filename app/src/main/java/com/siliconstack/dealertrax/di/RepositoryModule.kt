package com.siliconstack.dealertrax.di

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
    fun provideAppRepository(okHttpClient: OkHttpClient): HomeRepository {
        return HomeRepository(okHttpClient)
    }


}