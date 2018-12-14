package com.siliconstack.dealertrax.view.ui.setting

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.databinding.SettingActivityBinding
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import dagger.android.AndroidInjection
import org.jetbrains.anko.startActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.SignalStrength
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.siliconstack.dealertrax.view.ui.base.BaseActivity
import com.siliconstack.dealertrax.view.ui.MainActivity
import com.siliconstack.dealertrax.view.ui.MainActivityListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.siliconstack.dealertrax.model.*
import com.siliconstack.dealertrax.view.ui.search.SearchActivity
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.collections.forEachReversedWithIndex


class SettingActivity: BaseActivity() , MainActivityListener, SettingListener {


    //    @Inject
//    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var settingActivityBinding: SettingActivityBinding
    lateinit var materialDialog: MaterialDialog
    lateinit var adapter: SettingAdapter
    val spinnerArr= arrayListOf("Location", "Floor", "Name")
    var selectedPos=-1;

   // override fun supportFragmentInjector() = dispatchingAndroidInjector


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        initViewBinding()
        setListener()
        init()

    }

    private fun initViewBinding() {
        settingActivityBinding = DataBindingUtil.setContentView(this, R.layout.setting_activity)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
    }

    private fun init() {

        setTranslucentBarNoScrollView()
        settingActivityBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            this@SettingActivity.adapter = SettingAdapter(this@SettingActivity)
            adapter = this@SettingActivity.adapter
            var divider = DividerItemDecoration(context, RecyclerView.VERTICAL)
            divider.setDrawable(resources.getDrawable(R.drawable.list_divider_transparent))
            addItemDecoration(divider)
            refreshListLocation()
        }


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArr)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        settingActivityBinding.spinner.adapter=adapter
        settingActivityBinding.spinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                when(position){
                    0 -> {
                        refreshListLocation()
                        selectedPos=0
                    }
                    1 -> {
                        refreshListFloor()
                        selectedPos=1
                    }
                    2 -> {
                        refreshListName()
                        selectedPos=2
                    }
                }
            }

        }

    }

    fun refreshListLocation(){
        val items= arrayListOf<SettingDTO>()
        mainViewModel.locationDAO.getAll().forEach {
            items.add(SettingDTO(it.name,it.id, SettingAdapter.SettingEnum.LOCATION.ordinal))
        }
        adapter.items= items
        adapter.notifyDataSetChanged()
    }


    fun refreshListFloor(){
        val items= arrayListOf<SettingDTO>()
        mainViewModel.floorDAO.getAll().forEach {
            items.add(SettingDTO(it.name,it.id, SettingAdapter.SettingEnum.FLOOR.ordinal))
        }
        adapter.items= items
        adapter.notifyDataSetChanged()
    }

    fun refreshListName(){
        val items= arrayListOf<SettingDTO>()
        mainViewModel.nameDAO.getAll().forEach {
            items.add(SettingDTO(it.name,it.id, SettingAdapter.SettingEnum.NAME.ordinal))
        }
        adapter.items= items
        adapter.notifyDataSetChanged()
    }

    fun createNewDialog(){
        materialDialog=MaterialDialog.Builder(this)
                .content("Enter value")
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .contentGravity(GravityEnum.CENTER)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("","", MaterialDialog.InputCallback { dialog, input ->
                }).positiveText("Save").negativeText("Cancel").onPositive { dialog, which ->
                    if(!dialog.inputEditText?.text.toString().trim().isEmpty()) {
                        when (selectedPos) {
                            0 -> {
                                val locationModel=LocationModel(dialog.inputEditText?.text.toString(),0)
                                addLocation(locationModel)

                            }
                            1 -> {
                                val floorModel=FloorModel(dialog.inputEditText?.text.toString(),0)
                                addFloor(floorModel)
                            }
                            2 -> {
                                val operatorModel=OperatorModel(dialog.inputEditText?.text.toString(),0)
                                addOperator(operatorModel)
                            }
                        }
                    }
                }.onNegative { dialog, which ->
                    dialog.dismiss()
                }
                .build()
    }



    fun addLocation(locationModel: LocationModel){
        progressDialog.show()
        mainViewModel.postLocation(locationModel).observe(this, android.arch.lifecycle.Observer { it: Resource<BaseApiResponse>? ->
            it?.let { resource: Resource<BaseApiResponse> ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        var list: List<LocationModel>? = resource.data as List<LocationModel>
                        list?.forEachReversedWithIndex { i, locationModel ->
                            mainViewModel.locationDAO.addRow(LocationModel(locationModel.name,locationModel.id))
                            refreshListLocation()
                            return@let
                        }
                    }
                    Resource.Status.ERROR -> {
                        Toasty.error(this@SettingActivity,resource.exception?.exceptin?.message.toString()).show()
                    }
                }
            }
            progressDialog.dismiss()
        })
    }


    fun addFloor(floorModel: FloorModel){
        progressDialog.show()
        mainViewModel.postFloor(floorModel).observe(this, android.arch.lifecycle.Observer  { it: Resource<BaseApiResponse>? ->
            it?.let { resource: Resource<BaseApiResponse> ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        var list: List<FloorModel>? = resource.data as List<FloorModel>
                        list?.forEachReversedWithIndex { i, floorModel ->
                            mainViewModel.floorDAO.addRow(FloorModel(floorModel.name,floorModel.id))
                            refreshListFloor()
                            return@let
                        }
                    }
                    Resource.Status.ERROR -> {
                        Toasty.error(this@SettingActivity,resource.exception?.exceptin?.message.toString()).show()
                    }
                }
            }
            progressDialog.dismiss()
        })
    }

    fun addOperator(operatorModel: OperatorModel){
        progressDialog.show()
        mainViewModel.postOperator(operatorModel).observe(this, android.arch.lifecycle.Observer  { it: Resource<BaseApiResponse>? ->
            it?.let { resource: Resource<BaseApiResponse> ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        var list: List<OperatorModel>? = resource.data as List<OperatorModel>
                        list?.forEachReversedWithIndex { i, operatorModel ->
                            mainViewModel.nameDAO.addRow(OperatorModel(operatorModel.name,operatorModel.id))
                            refreshListName()
                            return@let
                        }

                    }
                    Resource.Status.ERROR -> {
                        Toasty.error(this@SettingActivity,resource.exception?.exceptin?.message.toString()).show()
                    }
                }
            }
            progressDialog.dismiss()
        })
    }

    private fun setListener() {
        settingActivityBinding.btnAdd.setOnClickListener { view->
            createNewDialog()
            materialDialog.show()
        }
        settingActivityBinding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        finish()
        startActivity<SearchActivity>()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onRemove(settingDTO: SettingDTO) {
        when(settingDTO.settingEnum){
            SettingAdapter.SettingEnum.LOCATION.ordinal -> {
                mainViewModel.locationDAO.deleteById(settingDTO.id)
                refreshListLocation()
            }
            SettingAdapter.SettingEnum.FLOOR.ordinal -> {
                mainViewModel.floorDAO.deleteById(settingDTO.id)
                refreshListFloor()
            }
            SettingAdapter.SettingEnum.NAME.ordinal -> {
                mainViewModel.nameDAO.deleteById(settingDTO.id)
                refreshListName()
            }
        }
    }
    override fun onSignalReceived(signalStrength: SignalStrength) {

    }

}

interface SettingListener{
    fun onRemove(settingDTO: SettingDTO)
}