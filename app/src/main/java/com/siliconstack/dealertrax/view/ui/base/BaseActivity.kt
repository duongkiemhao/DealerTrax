package com.siliconstack.dealertrax.view.ui.base
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.Crashlytics
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.view.helper.DialogHelper
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import io.fabric.sdk.android.Fabric
import javax.inject.Inject


open class BaseActivity : AppCompatActivity(){



    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel
    lateinit var progressDialog: MaterialDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)

        progressDialog = DialogHelper.materialProgressDialog(this)



    }

    open fun setTranslucentToolbar(){
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.parseColor("#533889"));

    }
    open fun setTranslucentBarNoScrollView(){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val w = window // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        }
    }

}



