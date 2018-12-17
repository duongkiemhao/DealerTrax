package com.siliconstack.dealertrax.di

import com.siliconstack.dealertrax.view.ui.*
import com.siliconstack.dealertrax.view.ui.base.BaseActivity
import com.siliconstack.dealertrax.view.ui.scan.ScanActivity
import com.siliconstack.dealertrax.view.ui.search.SearchActivity
import com.siliconstack.dealertrax.view.ui.setting.SettingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [EmptyFragmentBuildersModule::class])
    abstract fun contributeBaseActivity(): BaseActivity


    @ContributesAndroidInjector(modules = [EmptyFragmentBuildersModule::class])
    abstract fun injectLoginActivity(): MainActivity
    @ContributesAndroidInjector(modules = [SearchFragmentBuildersModule::class])
    abstract fun injectSearchActivity(): SearchActivity
    @ContributesAndroidInjector(modules = [EmptyFragmentBuildersModule::class])
    abstract fun injectSettingActivity(): SettingActivity
    @ContributesAndroidInjector(modules = [EmptyFragmentBuildersModule::class])
    abstract fun injectScanResultActivity(): ScanActivity
}