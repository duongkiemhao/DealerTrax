package com.siliconstack.dealertrax.view.ui.search

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import com.google.gson.stream.JsonReader
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.databinding.HomeActivityBinding
import com.siliconstack.dealertrax.model.FloorModel
import com.siliconstack.dealertrax.model.LocationModel
import com.siliconstack.dealertrax.model.MainModel
import com.siliconstack.dealertrax.model.OperatorModel
import com.siliconstack.dealertrax.view.control.GsonGenericClass
import com.siliconstack.dealertrax.view.eventbus.SearchEventBus
import com.siliconstack.dealertrax.view.ui.base.BaseActivity
import com.siliconstack.dealertrax.view.ui.scan.ScanActivity
import com.siliconstack.dealertrax.view.ui.setting.SettingActivity
import com.siliconstack.dealertrax.view.utility.Utility
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.nav_header.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import java.io.StringReader
import javax.inject.Inject


class SearchActivity : BaseActivity(), HasSupportFragmentInjector, SearchActivityListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var searchActivityBinding: HomeActivityBinding
    var doubleBackToExitPressedOnce: Boolean = false
    var isListView = false
    var mapType: Int? = Config.MAP_DEFAULT_TYPE

    enum class DRAWER_ITEM {
        NONE, SETTING
    }

    var openMenuItem = DRAWER_ITEM.NONE.ordinal
    var vehicleClickEnum=ScanActivity.SCANENUM.NONE.ordinal


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initViewBinding()
        init()
        setListener()
    }

    private fun initViewBinding() {
        searchActivityBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
    }

    fun setListener() {

        searchActivityBinding.btnSwitch.setOnClickListener {
            isListView = !isListView
            Utility.hideKeyboard(currentFocus,this)
            if (isListView) {
                searchActivityBinding.btnSwitch.text = "Map"
                searchActivityBinding.btnSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_location, 0, 0)
                toFragment()
            } else {
                searchActivityBinding.btnSwitch.text = "List"
                searchActivityBinding.btnSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_list, 0, 0)
                toFragment()
            }
        }
        searchActivityBinding.ediKeyword.addTextChangedListener(textChangeListener)

        searchActivityBinding.btnScanVin.setOnClickListener {
            vehicleClickEnum=ScanActivity.SCANENUM.VIN.ordinal
            searchActivityBinding.fab.close(false)
        }
        searchActivityBinding.btnScanRego.setOnClickListener {
            vehicleClickEnum=ScanActivity.SCANENUM.REGO.ordinal
            searchActivityBinding.fab.close(false)

        }
        searchActivityBinding.btnScanQrcode.setOnClickListener {
            vehicleClickEnum=ScanActivity.SCANENUM.QRCODE.ordinal
            searchActivityBinding.fab.close(false)
        }

        searchActivityBinding.btnScanBarcode.setOnClickListener {
            vehicleClickEnum=ScanActivity.SCANENUM.BARCODE.ordinal
            searchActivityBinding.fab.close(false)
        }
        searchActivityBinding.btnFocusAll.setOnClickListener {
            vehicleClickEnum=ScanActivity.SCANENUM.FOCUS.ordinal

            if (supportFragmentManager.findFragmentById(R.id.content) is MapViewFragmentListener) {
                searchActivityBinding.fab.close(false)
            }
        }
        searchActivityBinding.fab.setOnMenuToggleListener {
            if(!it){
                Utility.hideKeyboard(currentFocus,this)
                when(vehicleClickEnum){
                    ScanActivity.SCANENUM.FOCUS.ordinal ->{
                        (supportFragmentManager.findFragmentById(R.id.content) as MapViewFragmentListener).focusAllMarker()
                    }
                    ScanActivity.SCANENUM.NONE.ordinal ->{

                    }
                    else ->{

                        startActivity<ScanActivity>("scanEnum" to vehicleClickEnum,
                                "mapType" to mapType)
                        vehicleClickEnum=ScanActivity.SCANENUM.NONE.ordinal
                    }

                }
            }
        }

        searchActivityBinding.imgMenu.setOnClickListener {
            searchActivityBinding.drawerLayout.openDrawer(Gravity.LEFT)
            Utility.hideKeyboard(currentFocus,this)

        }
        searchActivityBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ite_setting -> {
                    openMenuItem = DRAWER_ITEM.SETTING.ordinal
                    searchActivityBinding.drawerLayout.closeDrawer(Gravity.LEFT)
                }


            }
            true
        }
        searchActivityBinding.drawerLayout.addDrawerListener(drawerListener)




    }

    val drawerListener=object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) {

        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        }

        override fun onDrawerClosed(drawerView: View) {
            when (openMenuItem) {
                DRAWER_ITEM.SETTING.ordinal -> {
                    startActivity<SettingActivity>()
                    openMenuItem = DRAWER_ITEM.NONE.ordinal
                }

            }
        }

        override fun onDrawerOpened(drawerView: View) {

        }

    }

    val textChangeListener=object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            //adapter.keyword=s.toString()
            mainViewModel.keyword = s.toString()
            (supportFragmentManager.findFragmentById(R.id.content) as FragmentListener).onTextChanged(s.toString().trim())


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override fun onBackPressed() {
        if (searchActivityBinding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
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
        syncData()

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
        this.mapType = mapType
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(searchEventBus: SearchEventBus) {
        searchEventBus.reload?.let {
            if (it)
                (supportFragmentManager.findFragmentById(R.id.content) as FragmentListener).reload()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @SuppressLint("CheckResult")
    fun syncData() {
        progressDialog.show()
        val requests = arrayListOf<Observable<List<Any>>>()
        requests.add(mainViewModel.getLocations())
        requests.add(mainViewModel.getFloors())
        requests.add(mainViewModel.getOperators())
        requests.add(mainViewModel.getStockChecks())
        Observable
                .zip(requests) { it: Array<out Any> ->
                    it
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it: Array<out Any>? ->
                    progressDialog.dismiss()
                    it?.forEachIndexed { index, any ->
                        val reader = JsonReader(StringReader(AppApplication.gson.toJson(any)))
                        reader.isLenient = true
                        when (index) {
                            0 -> {
                                if (!any.toString().isBlank()) {
                                    mainViewModel.locationDAO.deleteAll()
                                    val locations: List<LocationModel> = AppApplication.gson.fromJson(reader, GsonGenericClass<LocationModel>(LocationModel::class.java))
                                    locations.forEach {
                                        mainViewModel.locationDAO.addRow(LocationModel(it.name, it.id))
                                    }
                                }
                            }
                            1 -> {
                                if (!any.toString().isBlank()) {
                                    mainViewModel.floorDAO.deleteAll()
                                    val locations: List<FloorModel> = AppApplication.gson.fromJson(reader, GsonGenericClass<FloorModel>(FloorModel::class.java))
                                    locations.forEach {
                                        mainViewModel.floorDAO.addRow(FloorModel(it.name, it.id))
                                    }
                                }
                            }
                            2 -> {
                                if (!any.toString().isBlank()) {
                                    mainViewModel.nameDAO.deleteAll()
                                    val locations: List<OperatorModel> = AppApplication.gson.fromJson(reader, GsonGenericClass<OperatorModel>(OperatorModel::class.java))
                                    locations.forEach {
                                        mainViewModel.nameDAO.addRow(OperatorModel(it.name, it.id))
                                    }
                                }
                            }
                            3 -> {
                                if (!any.toString().isBlank()) {
                                    mainViewModel.mainDAO.deleteAll()
                                    var locations: List<MainModel> = AppApplication.gson.fromJson(reader, GsonGenericClass<MainModel>(MainModel::class.java))
                                    locations = locations.filter {
                                        !(it.latitude == null || it.longitude == null)
                                    }
                                    locations.forEach {
                                        mainViewModel.mainDAO.addRow(MainModel(it.id, it.scanText, it.timestamp, it.locationID,
                                                it.floorID, it.operatorID, it.bayNumber, it.scanTextTypeId, it.longitude, it.latitude))
                                    }
                                }
                            }

                        }
                    }
                    toFragment()

                }) {
                    progressDialog.dismiss()
                    Toasty.error(this@SearchActivity, it.message ?: "").show()
                    toFragment()
                }
    }

    fun toFragment() {
        searchActivityBinding.ediKeyword.removeTextChangedListener(textChangeListener)
        searchActivityBinding.ediKeyword.setText("")
        searchActivityBinding.ediKeyword.addTextChangedListener(textChangeListener)

        if (isListView) {
            supportFragmentManager.beginTransaction().replace(R.id.content, ListViewFragment.newInstance()).commit()
            searchActivityBinding.btnFocusAll.visibility = View.GONE
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.content, MapViewFragment.newInstance()).commit()
            searchActivityBinding.btnFocusAll.visibility = View.VISIBLE
        }
    }
}


interface FragmentListener {
    fun onTextChanged(text: String)
    fun reload()
}

interface SearchActivityListener {
    fun setmapType(mapType: Int)
}
