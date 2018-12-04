package com.siliconstack.dealertrax.view.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.databinding.DataBindingUtil
import android.databinding.adapters.ViewGroupBindingAdapter.setListener
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orhanobut.logger.Logger
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.databinding.MapviewFragmentBinding
import com.siliconstack.dealertrax.di.Injectable
import com.siliconstack.dealertrax.model.FilterDialogModel
import com.siliconstack.dealertrax.model.MainDTO
import com.siliconstack.dealertrax.view.adapter.FilterListAdapter
import com.siliconstack.dealertrax.view.helper.DialogHelper
import com.siliconstack.dealertrax.view.ui.scan.ScanResultActivity
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread
import java.lang.Exception
import javax.inject.Inject


class MapViewFragment : Fragment(), Injectable, MapViewFragmentListener ,FragmentListener{


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mapviewFragmentBinding: MapviewFragmentBinding
    lateinit var mainViewModel: MainViewModel


    //google map
    lateinit var googleMap: GoogleMap
    val REQUEST_CHECK_SETTINGS = 0x1
    val REQUEST_MY_LOCATION = 0x2
    var lat:Double = 0.0
    var lng:Double=0.0
    var listMapType = arrayListOf(FilterDialogModel("Satellite", "0", Config.MAP_DEFAULT_TYPE ==GoogleMap.MAP_TYPE_SATELLITE),
            FilterDialogModel("Terrain", "1", Config.MAP_DEFAULT_TYPE ==GoogleMap.MAP_TYPE_TERRAIN))
    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context!!);
    }
    val rxPermissions by lazy {
        RxPermissions(this)
    }
    val locationManager by lazy {
        context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    val array by lazy {
        ArrayList<Int>()
    }
    val googleApiClient by lazy {
        GoogleApiClient.Builder(context!!)
                .addApi(LocationServices.API)
                .build()
    }
    val locationRequest by lazy {
        LocationRequest.create();

    }
    val builder by lazy {
        LocationSettingsRequest.Builder()
    }


    companion object {
        fun newInstance(): MapViewFragment {
            val fragment = MapViewFragment()
            val args = Bundle()

            fragment.setArguments(args)
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mapviewFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.mapview_fragment, null, false);
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
        return mapviewFragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        init()

    }


    @SuppressLint("MissingPermission")
    private fun init() {
        mainViewModel.initItems()

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync {
            it?.let {
                googleMap = it
                googleMap.mapType = Config.MAP_DEFAULT_TYPE
                googleMap.setOnInfoWindowClickListener { it: Marker? ->
                    it?.hideInfoWindow()
                }
                //googleMap.uiSettings.isZoomControlsEnabled = true
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

                                focusAllMarkers()

                            }
                        }


            }
        }
    }



    @SuppressLint("MissingPermission")
    fun focusAllMarkers() {
        val builder = LatLngBounds.Builder()
        googleMap.clear()
          mainViewModel.items.forEach {
              if(it.lat==null || it.lat==0.0)
                  return@forEach
              val latLng=LatLng(it.lat?:0.0,it.lng?:0.0)
              val markerOption=MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker))

              var marker=googleMap.addMarker(markerOption)
              marker.tag=it
              builder.include(latLng)
          }
        if(mainViewModel.items.isNotEmpty()) {
            val bounds = builder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            googleMap.animateCamera(cameraUpdate)
        }
            val customInfoWindow = CustomInfoWindowGoogleMap(context!!)

        googleMap.setInfoWindowAdapter(customInfoWindow)


    }

    private fun setListener() {
        mapviewFragmentBinding.btnMaptype.setOnClickListener {
            DialogHelper.materialProgressDialog("Please select a store", context!!, FilterListAdapter(listMapType),
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
                                //(activity as SearchActivityListener).setmapType(googleMap.mapType)
                                return@SingleButtonCallback
                            }
                        }

                        dialog.dismiss()
                    }).show()

        }

    }


    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        val locationResult = fusedLocationProviderClient.lastLocation
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


    fun processItemNMEA(value:String,isLastOne:Boolean){
        if(isLastOne){
            if(value.isNotBlank() && !value.startsWith("*",true))
                array.add(value.substring(0, value.indexOf("*")).toInt())
        }
        else if(value.isNotBlank())
            array.add(value.toInt())


    }
    var nmeaListener=object: OnNmeaMessageListener {
        override fun onNmeaMessage(content: String?, p1: Long) {
            doAsync {
                if(content!!.toUpperCase().contains("GPGSV")) {
                    Logger.d(content)
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
                            mapviewFragmentBinding.txtGpsSignal.progress = max
                            array.clear()
                        }
                        catch (exp: Exception){
                            mapviewFragmentBinding.txtGpsSignal.progress = 0
                        }
                    }
                }
            }


        }


    }

    @SuppressLint("MissingPermission")
    fun settingsRequest(request:Int)
    {

        googleApiClient.connect()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000
        locationRequest.fastestInterval = 5 * 1000
        builder.addLocationRequest(locationRequest)
        builder.setAlwaysShow(true);
        val result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback { result: LocationSettingsResult ->
            val status = result.getStatus()
            val state = result.locationSettingsStates
            when(status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                {
                    requestPermissionNMEAListener()

                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        status.startResolutionForResult(activity, request)
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
                        locationManager.removeNmeaListener(nmeaListener)
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

        }
    }

    override fun onTextChanged(text: String) {
        mainViewModel.keyword=text
        mainViewModel.items= mainViewModel.filterListSearch(false,0,"a.timestamp") as java.util.ArrayList<MainDTO>
        focusAllMarkers()

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

    override fun focusAllMarker() {

        focusAllMarkers()
    }

    override fun reload() {
        mainViewModel.initItems()
        focusAllMarkers()
    }
}

interface MapViewFragmentListener {
    fun focusAllMarker()
}
