package com.siliconstack.dealertrax.view.ui.search

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import com.google.android.gms.maps.model.LatLng
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.databinding.SearchActivityBinding
import com.siliconstack.dealertrax.model.MainDTO
import com.siliconstack.dealertrax.view.ui.base.BaseActivity
import com.siliconstack.dealertrax.view.ui.scan.ScanResultActivity
import com.siliconstack.dealertrax.view.ui.setting.SettingActivity
import com.siliconstack.dealertrax.view.utility.Utility
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.nav_header.view.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.siliconstack.dealertrax.view.eventbus.MainEventBus
import com.siliconstack.dealertrax.view.eventbus.SearchEventBus
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SearchActivity : BaseActivity(), HasSupportFragmentInjector ,SearchActivityListener{

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var searchActivityBinding: SearchActivityBinding
    var doubleBackToExitPressedOnce: Boolean = false
    var isListView = false
    var mapType:Int?= Config.MAP_DEFAULT_TYPE
    enum class DRAWER_ITEM{
        NONE,SETTING
    }
    var openMenuItem=DRAWER_ITEM.NONE.ordinal


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initViewBinding()
        init()
        setListener()
    }

    private fun initViewBinding() {
        searchActivityBinding = DataBindingUtil.setContentView(this, R.layout.search_activity)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
    }

    fun setListener() {

        searchActivityBinding.btnSwitch.setOnClickListener {
            isListView = !isListView
            if (isListView) {
                supportFragmentManager.beginTransaction().replace(R.id.content, ListViewFragment.newInstance()).commit()
                searchActivityBinding.btnSwitch.text = "Map"
                searchActivityBinding.btnSwitch.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_location,0,0)
                searchActivityBinding.btnFocusAll.visibility=View.GONE
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.content, MapViewFragment.newInstance()).commit()
                searchActivityBinding.btnSwitch.text = "List"
                searchActivityBinding.btnSwitch.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_list,0,0)
                searchActivityBinding.btnFocusAll.visibility=View.VISIBLE
            }
        }
        searchActivityBinding.ediKeyword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //adapter.keyword=s.toString()
                mainViewModel.keyword = s.toString()
                (supportFragmentManager.findFragmentById(R.id.content) as FragmentListener).onTextChanged(s.toString().trim())


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        searchActivityBinding.btnScanVin.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.VIN.ordinal,
                    "mapType" to mapType)
        }
        searchActivityBinding.btnScanRego.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.REGO.ordinal,
                    "mapType" to mapType)
        }
        searchActivityBinding.btnScanQrcode.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.QRCODE.ordinal,
                    "mapType" to mapType)
        }

        searchActivityBinding.btnScanBarcode.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.BARCODE.ordinal,
                    "mapType" to mapType)
        }
        searchActivityBinding.imgMenu.setOnClickListener {
            searchActivityBinding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        searchActivityBinding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ite_setting -> {
                    openMenuItem=DRAWER_ITEM.SETTING.ordinal
                    searchActivityBinding.drawerLayout.closeDrawer(Gravity.LEFT)
                }

            }
            true
        }
        searchActivityBinding.drawerLayout.addDrawerListener(object:DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                when(openMenuItem){
                    DRAWER_ITEM.SETTING.ordinal ->{
                        startActivity<SettingActivity>()
                        openMenuItem=DRAWER_ITEM.NONE.ordinal
                    }
                }
            }

            override fun onDrawerOpened(drawerView: View) {

            }

        })

        searchActivityBinding.btnFocusAll.setOnClickListener {
            if(supportFragmentManager.findFragmentById(R.id.content) is MapViewFragmentListener){
                (supportFragmentManager.findFragmentById(R.id.content) as MapViewFragmentListener).focusAllMarker()
            }
        }
    }

    override fun onBackPressed() {
        if(searchActivityBinding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
            searchActivityBinding.drawerLayout.closeDrawer(Gravity.LEFT)
            return
        }
        if (doubleBackToExitPressedOnce) {
            finish()
        }
        doubleBackToExitPressedOnce = true
        Toasty.info(this, getString(R.string.msg_exit)).show()
        AppApplication.handler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun init() {
        setTranslucentBarNoScrollView()
        EventBus.getDefault().register(this)
        if(isListView) {
            supportFragmentManager.beginTransaction().replace(R.id.content, ListViewFragment.newInstance()).commit()
            searchActivityBinding.btnFocusAll.visibility=View.GONE
        }
        else {
            supportFragmentManager.beginTransaction().replace(R.id.content, MapViewFragment.newInstance()).commit()
            searchActivityBinding.btnFocusAll.visibility=View.VISIBLE
        }
        searchActivityBinding.navView.getHeaderView(0).txt_app_version.text = "Dealer Trax v" + Utility.getAppVersionName()
        setSupportActionBar(searchActivityBinding.toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(false)
        }

    }



    override fun supportFragmentInjector() = dispatchingAndroidInjector


    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val frg = supportFragmentManager.findFragmentById(R.id.content)
        frg?.onActivityResult(requestCode, resultCode, data)

    }


    override fun setmapType(mapType: Int) {
        this.mapType=mapType
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(searchEventBus: SearchEventBus) {
        searchEventBus.reload?.let {
            if(it)
            (supportFragmentManager.findFragmentById(R.id.content) as FragmentListener).reload()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}


interface FragmentListener {
    fun onTextChanged(text: String)
    fun reload()
}

interface SearchActivityListener {
    fun setmapType(mapType:Int)
}
