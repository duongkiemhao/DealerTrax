package com.siliconstack.dealertrax

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import com.marcinmoskala.kotlinpreferences.gson.GsonSerializer
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.siliconstack.dealertrax.di.AppComponent
import com.siliconstack.dealertrax.di.AppInjector
import com.siliconstack.dealertrax.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.DateTimeComparator
import javax.inject.Inject
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.view.utility.Utility.Companion.getDirSize
import org.jetbrains.anko.doAsync
import android.os.StrictMode




class AppApplication : MultiDexApplication(), HasActivityInjector  {

    companion object {
        @JvmStatic lateinit var appComponent: AppComponent
        lateinit var instance: AppApplication
            private set
        var gson = Gson()
        var handler = Handler()
        var isComeFromSC=false
        var imageId:String?=null


        val dateComparator= DateTimeComparator.getDateOnlyInstance()


    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {

        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        AppInjector.init(this)

        //Check cache dir size
        val cacheDir= cacheDir
        if(getDirSize(cacheDir)> Config.MAX_CACHE_DIR_SIZE) {
            doAsync {
                cacheDir.deleteRecursively()
            }
        }

        PreferenceHolder.setContext(applicationContext)
        PreferenceHolder.serializer = GsonSerializer(Gson())
        Kotpref.init(this)
        Logger.addLogAdapter(AndroidLogAdapter())
        initUserSetting()
        JodaTimeAndroid.init(this);
        FirebaseApp.initializeApp(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

    }

    fun initUserSetting(){


    }
    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

}
