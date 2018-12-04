package com.siliconstack.dealertrax.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.databinding.DataBindingUtil
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Bundle
import android.telephony.SignalStrength
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.config.Config.Companion.MAP_DEFAULT_TYPE
import com.siliconstack.dealertrax.databinding.MainActivityBinding
import com.siliconstack.dealertrax.model.FilterDialogModel
import com.siliconstack.dealertrax.view.adapter.FilterListAdapter
import com.siliconstack.dealertrax.view.helper.DialogHelper
import com.siliconstack.dealertrax.view.ui.base.BaseActivity
import com.siliconstack.dealertrax.view.ui.scan.ScanResultActivity
import com.siliconstack.dealertrax.view.ui.search.SearchActivity
import com.siliconstack.dealertrax.view.ui.setting.SettingActivity
import com.siliconstack.dealertrax.view.utility.Utility
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.lang.Exception


class MainActivity : BaseActivity() {

    //    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var mainActivityBinding: MainActivityBinding
    var doubleBackToExitPressedOnce: Boolean = false

    //google map
    lateinit var googleMap: GoogleMap
    val REQUEST_CHECK_SETTINGS = 0x1
    val REQUEST_MY_LOCATION = 0x2
    val REQUEST_TAKE_PICTURE = 0x3
    val REQUEST_OPEN_GALLERY = 0x4
    var lat:Double = 0.0
    var lng:Double=0.0
    var listMapType = arrayListOf(FilterDialogModel("Satellite", "0", MAP_DEFAULT_TYPE==GoogleMap.MAP_TYPE_SATELLITE),
    FilterDialogModel("Terrain", "1", MAP_DEFAULT_TYPE==GoogleMap.MAP_TYPE_TERRAIN))
    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this);
    }
    val rxPermissions by lazy {
        RxPermissions(this)
    }
    val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    val array by lazy {
        ArrayList<Int>()
    }
    val googleApiClient by lazy {
        GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build()
    }
    val locationRequest by lazy {
        LocationRequest.create();

    }
    val builder by lazy {
        LocationSettingsRequest.Builder()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setTranslucentBarNoScrollView()
        initViewBinding()
        setListener()
        init()

    }

    private fun initViewBinding() {
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
    }


    @SuppressLint("MissingPermission")
    private fun init() {
        setSupportActionBar(mainActivityBinding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        mainActivityBinding.txtAppVersion.text = "Dealer Trax v" + Utility.getAppVersionName()
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync {
            it?.let {
                googleMap = it
                googleMap.mapType = MAP_DEFAULT_TYPE
                googleMap.uiSettings.isZoomControlsEnabled = true
                rxPermissions
                        .request(Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe { it: Boolean? ->
                            if (it!!) {

                                googleMap.isMyLocationEnabled = true
                                googleMap.uiSettings.isMyLocationButtonEnabled = true
                                googleMap.setOnMyLocationButtonClickListener {
                                    settingsRequest(REQUEST_MY_LOCATION)
                                    false
                                }
                                getDeviceLocation()
                            }
                        }


            }
        }


    }


    @SuppressLint("MissingPermission", "CheckResult")
    private fun setListener() {
        mainActivityBinding.btnScanVin.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.VIN.ordinal,
                    "mapType" to googleMap.mapType,"lat" to googleMap.cameraPosition.target.latitude,"lng" to googleMap.cameraPosition.target.longitude)
        }
        mainActivityBinding.btnScanRego.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.REGO.ordinal,
                    "mapType" to googleMap.mapType,"lat" to googleMap.cameraPosition.target.latitude,"lng" to googleMap.cameraPosition.target.longitude)
        }
        mainActivityBinding.btnScanQrcode.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.QRCODE.ordinal,
                    "mapType" to googleMap.mapType,"lat" to googleMap.cameraPosition.target.latitude,"lng" to googleMap.cameraPosition.target.longitude)
        }

        mainActivityBinding.btnScanBarcode.setOnClickListener {
            startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.BARCODE.ordinal,
                    "mapType" to googleMap.mapType,"lat" to googleMap.cameraPosition.target.latitude,"lng" to googleMap.cameraPosition.target.longitude)
        }

        mainActivityBinding.btnMaptype.setOnClickListener {

            DialogHelper.materialProgressDialog("Please select a store", this, FilterListAdapter(listMapType),
                    MaterialDialog.SingleButtonCallback { dialog, which ->
                        listMapType = (dialog.recyclerView.adapter as FilterListAdapter).items
                        listMapType.forEach {
                            if (it.isSelect) {

                                when (it!!.code) {
                                    "0" -> {
                                        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

                                    }
                                    "1" -> googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                                }
                                return@SingleButtonCallback
                            }
                        }

                        dialog.dismiss()

                    }).show()

        }



    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.btn_view -> {
                startActivity<SearchActivity>()
                return true
            }
            R.id.btn_setting -> {
                startActivity<SettingActivity>()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
        }
        doubleBackToExitPressedOnce = true
        Toasty.info(this, getString(R.string.msg_exit)).show()
        AppApplication.handler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()

    }



    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        val locationResult = fusedLocationProviderClient.getLastLocation()
        locationResult.addOnCompleteListener {
            if (it.isSuccessful) {
                val location = it.result
                location?.let {
                    val update = CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude),
                            Config.MAP_ZOOM);
                    googleMap.animateCamera(update)
                    lat=it.latitude
                    lng=it.longitude
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        locationManager.removeNmeaListener(nmeaListener)
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        settingsRequest(REQUEST_CHECK_SETTINGS)
    }

    fun processItemNMEA(value:String,isLastOne:Boolean){
        if(isLastOne){
            if(value.isNotBlank() && !value.startsWith("*",true))
                array.add(value.substring(0, value.indexOf("*")).toInt())
        }
        else if(value.isNotBlank())
            array.add(value.toInt())


    }
    var nmeaListener=object: OnNmeaMessageListener{
        override fun onNmeaMessage(content: String?, p1: Long) {
            doAsync {
                if(content!!.toUpperCase().contains("GPGSV")) {
                    val arr: List<String> = content.split(",")
                    when {
                        arr.size == 8 -> {
                            processItemNMEA(arr[7],true)
                        } // Display the string.
                        arr.size == 12 -> {
                            processItemNMEA(arr[7],false)
                            processItemNMEA(arr[11],true)
                        }
                        arr.size == 16 -> {
                            processItemNMEA(arr[7],false)
                            processItemNMEA(arr[11],false)
                            processItemNMEA(arr[15],true)
                        } // Display the string.
                        arr.size == 20 -> {
                            processItemNMEA(arr[7],false)
                            processItemNMEA(arr[11],false)
                            processItemNMEA(arr[15],false)
                            processItemNMEA(arr[19],true)
                        } // Display the string.
                    }
                }
                uiThread {
                    if(array.count()>=1){
                        try {
                            var max = array.stream().mapToInt { it: Int? ->
                                it!!
                            }.max().asInt
                            mainActivityBinding.txtGpsSignal.progress = max
                            array.clear()
                        }
                        catch (exp:Exception){
                            mainActivityBinding.txtGpsSignal.progress = 0
                        }
                    }
                }
            }


        }


    }

    @SuppressLint("MissingPermission")
    fun settingsRequest(request:Int)
    {

        googleApiClient.connect();
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationRequest.interval = 30 * 1000;
        locationRequest.fastestInterval = 5 * 1000;
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        val result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback { result: LocationSettingsResult ->
            val status = result.status
                val state = result.locationSettingsStates
                when(status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS ->
                    {
                        requestPermissionNMEAListener()
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            status.startResolutionForResult(this@MainActivity, request);
                        } catch (e: IntentSender.SendIntentException) {

                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                    {

                    }
                }
        }
    }

    @SuppressLint("MissingPermission", "CheckResult")
    fun requestPermissionNMEAListener(){
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { it: Boolean? ->
                    if (it!!) {
                        locationManager.addNmeaListener(nmeaListener)
                    }
                }
    }
    
    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CHECK_SETTINGS ->{
                when(resultCode){
                    Activity.RESULT_OK ->{
                      requestPermissionNMEAListener()
                    }
                    Activity.RESULT_CANCELED->{

                    }
                }
            }
            REQUEST_MY_LOCATION->{
                when(resultCode){
                    Activity.RESULT_OK ->{
                        rxPermissions
                                .request( Manifest.permission.ACCESS_FINE_LOCATION)
                                .subscribe { it: Boolean? ->
                                    if (it!!) {
                                        getDeviceLocation()
                                    }
                                }
                    }
                    Activity.RESULT_CANCELED->{

                    }
                }
            }
            REQUEST_TAKE_PICTURE ->{
                startActivity<ScanResultActivity>("scanEnum" to ScanResultActivity.SCAN_ENUM.VIN.ordinal,
                        "mapType" to googleMap.mapType,"lat" to googleMap.cameraPosition.target.latitude,"lng" to googleMap.cameraPosition.target.longitude)
            }
            REQUEST_OPEN_GALLERY ->{

            }
        }
    }


}

interface MainActivityListener {
    fun onSignalReceived(signalStrength: SignalStrength)
}