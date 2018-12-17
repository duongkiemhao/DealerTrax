package com.siliconstack.dealertrax.view.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.PreferenceSetting
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.config.Constant
import com.siliconstack.dealertrax.databinding.ScanActivityBinding
import com.siliconstack.dealertrax.model.*
import com.siliconstack.dealertrax.view.adapter.FilterListAdapter
import com.siliconstack.dealertrax.view.eventbus.MainEventBus
import com.siliconstack.dealertrax.view.eventbus.SearchEventBus
import com.siliconstack.dealertrax.view.helper.DialogHelper
import com.siliconstack.dealertrax.view.ui.base.BaseActivity
import com.siliconstack.dealertrax.view.utility.DateUtility
import com.siliconstack.dealertrax.view.utility.Utility
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import com.siliconstack.dealertrax.viewmodel.ScanViewModel
import com.siliconstack.stockcheck.model.OCRModel
import com.siliconstack.stockcheck.model.OCRRequest
import com.tbruyelle.rxpermissions2.RxPermissions
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class ScanActivity : BaseActivity(){

    lateinit var scanActivityBinding: ScanActivityBinding
    lateinit var scanViewModel: ScanViewModel
    val REQUEST_QRCODE=101
    val REQUEST_BARCODE=100
    val REQUEST_CHOOSE_FROM_GALLERY=102
    var result:String?=null
    var scanEnum:Int = 0
    //spinner
     var listLocation:ArrayList<FilterDialogModel> = arrayListOf()
    var listFloor:ArrayList<FilterDialogModel> = arrayListOf()
    var listName:ArrayList<FilterDialogModel> = arrayListOf()
     var locationModel:FilterDialogModel?=null
    var floorModel:FilterDialogModel?=null
    var nameModel:FilterDialogModel?=null


    //google map
    lateinit var googleMap: GoogleMap
    val REQUEST_CHECK_SETTINGS = 0x1
    val REQUEST_MY_LOCATION = 0x2
    var lat:Double = 0.0
    var lng:Double=0.0
    
    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    val rxPermissions by lazy {
        RxPermissions(this)
    }
    val locationManager by lazy {
        this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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



    //scan
    enum class SCANENUM{
        NONE,VIN, REGO,BARCODE,QRCODE,FOCUS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initViewBinding()
        setTranslucentToolbar()

        EventBus.getDefault().register(this)
        scanEnum=intent.getIntExtra("scanEnum",0)
        setListener()
        initInfo()

        //showPickChooseIntent()

    }

    private fun initViewBinding() {
        scanActivityBinding = DataBindingUtil.setContentView(this, R.layout.scan_activity)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        scanViewModel= ViewModelProviders.of(this, viewModelFactory).get(ScanViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
        AppApplication.appComponent.injectViewModel(scanViewModel)
    }


    @SuppressLint("CheckResult")
    fun openCameraActivity(){
        rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe { it: Boolean? ->
                    if (it!!) {
                        when(scanEnum){
                            SCANENUM.VIN.ordinal ->
                                startActivity<CameraActivity>()
                            SCANENUM.REGO.ordinal ->
                                startActivity<CameraActivity>()
                            SCANENUM.BARCODE.ordinal -> {
                                val intent = Intent(this, ZXingScannerActivity::class.java)
                                startActivityForResult(intent, REQUEST_BARCODE)
                            }
                            else -> {
                                val intent = Intent(this, ZXingScannerActivity::class.java)
                                startActivityForResult(intent, REQUEST_QRCODE)
                            }

                        }
                    }
                }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_QRCODE ->{
                when(resultCode){
                    Activity.RESULT_OK ->{
                        val result=data!!.getStringExtra("result")
                        scanActivityBinding.ediScanResult.setText(result)
                    }
                }
            }
            REQUEST_BARCODE->{
                when(resultCode){
                    Activity.RESULT_OK ->{
                        val result=data!!.getStringExtra("result")
                        scanActivityBinding.ediScanResult.setText(result)
                    }
                }
            }
            REQUEST_CHOOSE_FROM_GALLERY ->{
                when(resultCode){
                    Activity.RESULT_OK->{
                        val uri=data?.data
                        CropImage.activity(uri)
                                .start(this);
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->{
                val result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        val resultUri = result.uri
                        val resizedBitmap = Utility.scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri),840)
                        scanActivityBinding.imageView.setImageBitmap(resizedBitmap)
                        when(scanEnum){
                            SCANENUM.VIN.ordinal ->{
                                scanVinAPI(resizedBitmap)
                            }
                            SCANENUM.REGO.ordinal ->{
                                scanRegoAPI(resizedBitmap)
                            }
                            else ->{}
                        }


                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    }
            }
            REQUEST_CHECK_SETTINGS ->{
                when(resultCode){
                    Activity.RESULT_OK ->{
                        getDeviceLocation()
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

    fun setListener() {
        scanActivityBinding.btnCancel.setOnClickListener {
            finish()
        }
        scanActivityBinding.btnConfirm.setOnClickListener {
            if(scanActivityBinding.ediScanResult.text.isNullOrBlank())
                return@setOnClickListener
            val scanItem=mainViewModel.isScanTextExist(scanActivityBinding.ediScanResult.text.toString().trim())
            if(scanItem!=null){
                val view=LayoutInflater.from(this).inflate(R.layout.view_vehicle_found,null)
                view.findViewById<TextView>(R.id.txt_value).text=scanItem.scanText
                view.findViewById<TextView>(R.id.txt_location).text=scanItem.locationName
                view.findViewById<TextView>(R.id.txt_floor).text=scanItem.floorName
                view.findViewById<TextView>(R.id.txt_bay).text=scanItem.bayNumber
                view.findViewById<TextView>(R.id.txt_operator).text=scanItem.operatorName
                view.findViewById<TextView>(R.id.txt_timestamp).text=DateUtility.parseDateToDateTimeStr(Constant.COMBINE_DATE_TIME_FORMAT,Date(scanItem.timestamp?:0))
                DialogHelper.materialCustomViewDialog("Matching Vehicle Found!",view,"Ok","Cancel", MaterialDialog.SingleButtonCallback { dialog, which ->
                    insertToDB()
                    dialog.dismiss()
                }, MaterialDialog.SingleButtonCallback { dialog, which ->
                    dialog.dismiss()
                },this@ScanActivity).show()

            }
            else {
                insertToDB()
            }
        }
        scanActivityBinding.btnCamera.setOnClickListener {
            openCameraActivity()
        }
        scanActivityBinding.btnGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, REQUEST_CHOOSE_FROM_GALLERY)
        }

        scanActivityBinding.txtLocation.setOnClickListener {
            DialogHelper.materialProgressDialog("", this, FilterListAdapter(listLocation),
                    MaterialDialog.SingleButtonCallback { dialog, which ->
                        listLocation= (dialog.recyclerView.adapter as FilterListAdapter).items
                        listLocation.forEach {
                            if (it.isSelect) {
                                locationModel=it
                                scanActivityBinding.txtLocation.text=locationModel?.name
                                PreferenceSetting.locationSetting=it
                                return@SingleButtonCallback
                            }
                        }
                        dialog.dismiss()
                    }).show()
        }
        scanActivityBinding.txtFloor.setOnClickListener {
            DialogHelper.materialProgressDialog("", this, FilterListAdapter(listFloor),
                    MaterialDialog.SingleButtonCallback { dialog, which ->
                        listFloor= (dialog.recyclerView.adapter as FilterListAdapter).items
                        listFloor.forEach {
                            if (it.isSelect) {
                                floorModel=it
                                scanActivityBinding.txtFloor.text=floorModel?.name
                                PreferenceSetting.floorSetting=it
                                return@SingleButtonCallback
                            }
                        }
                        dialog.dismiss()
                    }).show()
        }
        scanActivityBinding.txtOperator.setOnClickListener {
            DialogHelper.materialProgressDialog("", this, FilterListAdapter(listName),
                    MaterialDialog.SingleButtonCallback { dialog, which ->
                        listName= (dialog.recyclerView.adapter as FilterListAdapter).items
                        listName.forEach {
                            if (it.isSelect) {
                                nameModel=it
                                scanActivityBinding.txtOperator.text=nameModel?.name
                                PreferenceSetting.nameSetting=it
                                return@SingleButtonCallback
                            }
                        }
                        dialog.dismiss()
                    }).show()
        }

    }

    fun insertToDB(){
        val locationId = locationModel?.code?.toIntOrNull()?:0
        val floorId =  floorModel?.code?.toIntOrNull()?:0
        val nameId =  nameModel?.code?.toIntOrNull()?:0
        val date = Date()
        val mainModel = MainModel(0, scanActivityBinding.ediScanResult.text.toString(), date.time,
                if (locationId == 0) null else locationId, if (floorId == 0) null else floorId
                , if (nameId == 0) null else nameId, scanActivityBinding.ediBayNumber.text.toString(),getType(), googleMap.cameraPosition.target.longitude,
                googleMap.cameraPosition.target.latitude)
        //get screenshot
        googleMap.addMarker(MarkerOptions().position(com.google.android.gms.maps.model.LatLng(googleMap.cameraPosition.target.latitude,
                googleMap.cameraPosition.target.longitude)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)))
        scanActivityBinding.btnMarker.visibility=View.GONE
        insertAPI(mainModel)
//        googleMap.snapshot {
//            mainModel.image=Utility.convertBitmapToBase64(Utility.scaleBitmapDown(it,860))

//        }

    }


    fun insertAPI(mainModel: MainModel){
        progressDialog.show()
        mainViewModel.postStockCheck(mainModel).observe(this, android.arch.lifecycle.Observer { it: Resource<BaseApiResponse>? ->
            it?.let { resource: Resource<BaseApiResponse> ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        val list: List<MainModel>? = resource.data as List<MainModel>
                        list?.forEach{ mainModel ->

                            mainViewModel.addMainModel(mainModel)
                            EventBus.getDefault().post(SearchEventBus(true))
                            onBackPressed()
                        }
                    }
                    Resource.Status.ERROR -> {
                        Toasty.error(this@ScanActivity,resource.exception?.exceptin?.message.toString()).show()
                    }
                    else -> {
                    }
                }
            }
            progressDialog.dismiss()
        })
    }

    fun getType():Int{
        when(scanEnum){
            SCANENUM.VIN.ordinal ->
                return 0
            SCANENUM.REGO.ordinal ->
                return 1
            SCANENUM.BARCODE.ordinal ->
                return 2
            else -> return 3

        }
    }

    fun getToolbarTitle():String{
        when(scanEnum){
            SCANENUM.VIN.ordinal ->
                return "SCAN VIN"
            SCANENUM.REGO.ordinal ->
                return "SCAN REGO"
            SCANENUM.BARCODE.ordinal ->
                return "SCAN BARCODE"
            else -> return "SCAN QRCODE"

        }
    }
    @SuppressLint("MissingPermission", "ResourceType")
    fun initInfo() {
        scanActivityBinding.txtTitle.text=getToolbarTitle()

        scanActivityBinding.txtLocation.text=PreferenceSetting.locationSetting?.name?:""
        locationModel=PreferenceSetting.locationSetting
        scanActivityBinding.txtFloor.text=PreferenceSetting.floorSetting?.name?:""
        floorModel=PreferenceSetting.floorSetting
        scanActivityBinding.txtOperator.text=PreferenceSetting.nameSetting?.name?:""
        nameModel=PreferenceSetting.nameSetting
        //google map
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync {
            it?.let {
                googleMap = it
                googleMap.mapType = intent.getIntExtra("mapType",0)
                rxPermissions
                        .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe { it: Boolean? ->
                            if (it!!) {
                                googleMap.isMyLocationEnabled = true
                                googleMap.uiSettings.isMyLocationButtonEnabled = true
//                                val latLng=LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lng",0.0))
//                                val update = CameraUpdateFactory.newLatLngZoom(latLng,
//                                        MAP_ZOOM);
//                                googleMap.animateCamera(update)
                                googleMap.setOnMyLocationButtonClickListener {
                                    settingsRequest(REQUEST_MY_LOCATION)
                                    false
                                }
                                (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).
                                        view?.findViewById<View>(0x2)?.performClick()

                            }
                        }

            }
        }

        Handler().postDelayed({
            val layoutHeight=scanActivityBinding.layoutImage.height
            val imageHeight=scanActivityBinding.btnMarker.height
            (scanActivityBinding.btnMarker.layoutParams as ConstraintLayout.LayoutParams).setMargins(0,(layoutHeight/2)-(imageHeight),0,0)
            scanActivityBinding.btnMarker.requestLayout()
        },500)

        if(scanEnum==SCANENUM.VIN.ordinal || scanEnum==SCANENUM.REGO.ordinal)
        {
            scanActivityBinding.btnGallery.visibility=View.VISIBLE
            scanActivityBinding.btnCamera.visibility=View.VISIBLE
        }
        else{
            scanActivityBinding.btnGallery.visibility=View.GONE
            scanActivityBinding.btnCamera.visibility=View.VISIBLE
        }

        this.listLocation.add(FilterDialogModel("None","0","0"==PreferenceSetting.locationSetting?.code?:0))
        mainViewModel.locationDAO.getAll().forEach {
            this.listLocation.add(FilterDialogModel(it.name,it.id.toString(),it.id.toString()==PreferenceSetting.locationSetting?.code?:"0"))
        }
        this.listFloor.add(FilterDialogModel("None","0","0"==PreferenceSetting.floorSetting?.code?:"0"))
        mainViewModel.floorDAO.getAll().forEach {
            this.listFloor.add(FilterDialogModel(it.name,it.id.toString(),it.id.toString()==PreferenceSetting.floorSetting?.code?:"0"))
        }
        this.listName.add(FilterDialogModel("None","0","0"==PreferenceSetting.nameSetting?.code?:"0"))
        mainViewModel.nameDAO.getAll().forEach {
            this.listName.add(FilterDialogModel(it.name,it.id.toString(),it.id.toString()==PreferenceSetting.nameSetting?.code?:"0"))
        }
    }


    fun scanVinAPI(bitmap: Bitmap){
        var ocrRequest= OCRRequest()
        ocrRequest.imageData=Utility.convertBitmapToBase64(bitmap)
        progressDialog.show()
        scanViewModel.getVin(ocrRequest).observe(this, android.arch.lifecycle.Observer { resource: Resource<BaseApiResponse>? ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        val ocrModel= resource.data as OCRModel
                        if(ocrModel!=null){
                            scanActivityBinding.ediScanResult.setText(ocrModel!!.vin)
                        }
                        else{
                            Toasty.info(this@ScanActivity,"Sorry, No text found, please try again").show()
                        }

                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toasty.error(this@ScanActivity,resource.exception?.exceptin?.message?:"").show()

                    }
                }
            }
        })
    }

    fun scanRegoAPI(bitmap: Bitmap){
        var ocrRequest= OCRRequest()
        ocrRequest.imageData=Utility.convertBitmapToBase64(bitmap)
        progressDialog.show()
        scanViewModel.getRego(ocrRequest).observe(this, android.arch.lifecycle.Observer { resource: Resource<BaseApiResponse>? ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        val ocrModel= resource.data as OCRModel
                        if(ocrModel!=null){
                            scanActivityBinding.ediScanResult.setText(ocrModel!!.rego)
                        }
                        else{
                            Toasty.info(this@ScanActivity,"Sorry, No text found, please try again").show()
                        }

                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toasty.error(this@ScanActivity,resource.exception?.exceptin?.message?:"").show()

                    }
                }
            }
        })
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(mainEventBus: MainEventBus) {
        mainEventBus.bitmapURL?.let {
            val bitmap=Utility.getBitmapFromURL(it)
            scanActivityBinding.imageView.setImageBitmap(bitmap)
            when(scanEnum){
                SCANENUM.VIN.ordinal ->{
                    scanVinAPI(bitmap)
                }
                SCANENUM.REGO.ordinal ->{
                    scanRegoAPI(bitmap)
                }
                else ->{}
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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
            when(status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                {
                    getDeviceLocation()

                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        status.startResolutionForResult(this@ScanActivity, request)
                    } catch (e: IntentSender.SendIntentException) {

                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                {

                }
            }
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

    override fun onBackPressed() {
        finish()
    }

}
