package com.siliconstack.dealertrax.di


import android.arch.lifecycle.ViewModel
import com.siliconstack.dealertrax.AppApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

import javax.inject.Singleton

/**
 * Created by Antoni Castejón on 03/01/2018.
 */
@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, ActivityModule::class,AppModule::class))
interface AppComponent{
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: AppApplication): Builder

        fun build(): AppComponent
    }
    fun inject(appApplication: AppApplication)
    fun injectViewModel(viewModel: ViewModel)


}

