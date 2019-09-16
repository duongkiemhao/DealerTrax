package com.siliconstack.dealertrax.view.ui.search

import android.Manifest
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.config.Config
import com.siliconstack.dealertrax.config.Constant
import com.siliconstack.dealertrax.databinding.ListviewFragmentBinding
import com.siliconstack.dealertrax.di.Injectable
import com.siliconstack.dealertrax.model.MainDTO
import com.siliconstack.dealertrax.model.SearchDTO
import com.siliconstack.dealertrax.view.control.CSVWriter
import com.siliconstack.dealertrax.view.helper.DialogHelper
import com.siliconstack.dealertrax.view.utility.DateUtility
import com.siliconstack.dealertrax.view.utility.Utility
import com.siliconstack.dealertrax.viewmodel.MainViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.startActivity
import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.joda.time.Period
import java.io.File
import java.io.FileWriter
import java.util.*
import javax.inject.Inject


class ListViewFragment : android.support.v4.app.Fragment(), Injectable ,ListViewFragmentListener,FragmentListener{


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var listviewFragmentBinding:ListviewFragmentBinding
    lateinit var mainViewModel: MainViewModel


    lateinit var adapter: SearchAdapter
    var isDesc=true
    val rxPermissions by lazy {
        RxPermissions(this)
    }

    var offset=0
    var isLoading=false
    var isDateSorting=true


    companion object {
        fun newInstance(): ListViewFragment {
            val fragment = ListViewFragment()
            val args = Bundle()

            fragment.setArguments(args)
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        listviewFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.listview_fragment, null, false)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        AppApplication.appComponent.injectViewModel(mainViewModel)
        return listviewFragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        init()

    }



    private fun init() {

        var divider = DividerItemDecoration(context!!, RecyclerView.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.list_divider_transparent))
        listviewFragmentBinding.recyclerView.addItemDecoration(divider)

        mainViewModel.initItems()
        bindAdapter()

        listviewFragmentBinding.txtLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        listviewFragmentBinding.recyclerView.postDelayed({
            listviewFragmentBinding.recyclerView.addOnScrollListener(mScrollListener)
        }
                ,2000)
        if(mainViewModel.items.count()< Config.LIMIT)
            isLoading=true

    }

    private fun setListener() {

        listviewFragmentBinding.btnExport.setOnClickListener {
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe { it: Boolean? ->
                        if (it!!) {
                            export()
                        }
                    }
        }


        listviewFragmentBinding.txtDate.setOnClickListener {
            isDateSorting=true
            isDesc=!isDesc
            listviewFragmentBinding.txtLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
            listviewFragmentBinding.txtDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,if(isDesc) R.drawable.ic_down else R.drawable.ic_up,0)
            offset=0
            isLoading=false
            mainViewModel.items= mainViewModel.filterListSearch(isDesc,offset,if(isDateSorting) "a.timestamp" else "b.name") as ArrayList<MainDTO>
            bindAdapter()
            listviewFragmentBinding.recyclerView.postDelayed({
                listviewFragmentBinding.recyclerView.scrollToPosition(0)
            },300)


        }
        listviewFragmentBinding.txtLocation.setOnClickListener {
            isDateSorting=false
            isDesc=!isDesc
            listviewFragmentBinding.txtDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
            listviewFragmentBinding.txtLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,if(isDesc) R.drawable.ic_down else R.drawable.ic_up,0)
            offset=0
            isLoading=false

            mainViewModel.items= mainViewModel.filterListSearch(isDesc,offset,if(isDateSorting) "a.timestamp" else "b.name") as ArrayList<MainDTO>
            bindAdapter()
            listviewFragmentBinding.recyclerView.postDelayed({
                listviewFragmentBinding.recyclerView.scrollToPosition(0)
            },300)


        }
    }

    var mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (isLoading)
                return
            val visibleItemCount = listviewFragmentBinding.recyclerView.layoutManager?.childCount?:0
            val totalItemCount = listviewFragmentBinding.recyclerView.layoutManager?.itemCount?:0
            val pastVisibleItems = (listviewFragmentBinding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()


            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                listviewFragmentBinding.recyclerView.removeOnScrollListener(this)

                Handler().postDelayed({
                    offset+= Config.LIMIT
                    val items=mainViewModel.filterListSearch(isDesc,offset,if(isDateSorting) "a.timestamp" else "b.name")
                    mainViewModel.items.addAll(items)

                    adapter = SearchAdapter(this@ListViewFragment, getListExpandGroup())
                    listviewFragmentBinding.recyclerView.adapter=adapter
                    isLoading=items.count()< Config.LIMIT

                    Handler().postDelayed({
                        listviewFragmentBinding.recyclerView.addOnScrollListener(this)
                    },1000)


                },1000)

                isLoading = true


            }
        }
    }

    fun getListExpandGroup():List<SearchDTO>{
        //process timestamp compare
        var hashMap=HashMap<String,Long>()
        var key=""
        var value:Long?=0
        if(isDateSorting) {
            if (isDesc) {
                mainViewModel.items.forEachReversedWithIndex { i, it ->
                    processCompareTimeInList(key, it, value, hashMap)
                }
            } else mainViewModel.items.forEachIndexed { index, it ->
                processCompareTimeInList(key, it, value, hashMap)
            }
        }

        //split into expandable list master/child
        var items= ArrayList<SearchDTO>()
        var title:String=""
        var listMainDTO:ArrayList<MainDTO> = arrayListOf()
        mainViewModel.items.forEachIndexed { index, model ->
            if(index==0) {
                if(isDateSorting)
                    title= DateUtility.parseDateToDateTimeStr(Constant.UI_DATE_FORMAT, Date(model.timestamp?:0))?:""
                else title=model.locationName?:""
                listMainDTO.add(model)

            }
            else{
                if(isDateSorting) {
                    if (Utility.compare2DatePart(Date(model.timestamp
                                    ?: 0), Date(mainViewModel.items.get(index - 1).timestamp
                                    ?: 0)) == 0) {
                        listMainDTO.add(model)
                    } else {
                        items.add(SearchDTO(title, listMainDTO))
                        title = DateUtility.parseDateToDateTimeStr(Constant.UI_DATE_FORMAT, Date(model.timestamp
                                ?: 0)) ?: ""
                        listMainDTO = arrayListOf()
                        listMainDTO.add(model)
                    }
                }
                else{
                    if (model.locationName==mainViewModel.items.get(index - 1).locationName) {
                        listMainDTO.add(model)
                    } else {
                        items.add(SearchDTO(title, listMainDTO))
                        title=model.locationName?:""
                        listMainDTO = arrayListOf()
                        listMainDTO.add(model)
                    }
                }
            }
            if(index==mainViewModel.items.count()-1){
                items.add(SearchDTO(title,listMainDTO))
            }
        }
        if(!isDateSorting){
            items.forEach {
                Collections.sort(it.items,MainDTO(isDesc))
                if(isDesc) {
                    it.items.forEachReversedWithIndex { i, it ->
                        processCompareTimeInList(key, it, value, hashMap)
                    }
                }
                else{
                    it.items.forEachIndexed { index, it ->
                        processCompareTimeInList(key, it, value, hashMap)
                    }
                }
            }
        }

        return items

    }

    private fun processCompareTimeInList(key: String, it: MainDTO, value: Long?, hashMap: HashMap<String, Long>) {
        var key1 = key
        var value1 = value
        key1 = it.scanText + "-" + it.locationID + "-" + it.floorID + "-" + it.bayNumber
        value1 = hashMap.get(key1)
        if (hashMap.size == 0) {
            hashMap.put(key1, it.timestamp ?: 0)
        } else {
            if (value1 == null)
                hashMap.put(key1, it.timestamp ?: 0)
            else {
                var period: Period
                period = Period(value1 ?: 0, it.timestamp ?: 0)

                val days= Days.daysBetween(LocalDateTime(value1?:0), LocalDateTime(it.timestamp?:0)).days
                if(days>0)
                    it.compareTimeFullStr=days.toString()+"day(s)"
                else {
                    it.compareTimeFullStr=period.hours.toString()+"hour(s) "+period.minutes+"minute(s)"
                }

                it.compareTime = if (period.years > 0) period.years.toString() + "y" else
                    if (period.months > 0) period.months.toString() + "M" else
                        if (period.weeks > 0) period.weeks.toString() + "w" else
                            if (period.days > 0) period.days.toString() + "d" else
                                if (period.hours > 0) period.hours.toString() + "h" else
                                    if (period.minutes > 0) period.minutes.toString() + "m" else
                                        ""
                //hashMap.put(key1, it.timestamp ?: 0)
            }
        }
    }

    fun export() {
        val exportDir = File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        val filePath = exportDir.absolutePath + "/" + resources.getString(R.string.app_name) + ".csv"
        val file = File(filePath)
        try {
            file.createNewFile();
            val csvWrite = CSVWriter(FileWriter(file));
            val columnName = arrayOf("Value","Location","Floor","Bay","Name","Date")
            csvWrite.writeNext(columnName);
            var isFound=false
            adapter.groups?.forEach {
                it.items.forEach {
                    val item=it as MainDTO
                    if(item.isSelected) {
                        isFound=true
                        val arrStr = arrayOf(item.scanText, item.locationName,item.floorName,item.bayNumber,item.operatorName,item.dateString)
                        csvWrite.writeNext(arrStr);
                    }
                }

            }
            csvWrite.close();

            if (!isFound) {
                Toasty.info(context!!, "No selected records to export").show()
            } else {
                DialogHelper.materialDialog("Exported to " + filePath, "Close", "Mail",
                        MaterialDialog.SingleButtonCallback { dialog, which ->
                            dialog.dismiss()
                        }, MaterialDialog.SingleButtonCallback { dialog, which ->
                    //openFile(file)
                    sendMail(file)
                    dialog.dismiss()
                }, context!!).show()
            }
        } catch (exp: java.lang.Exception) {
            Toasty.error(context!!, exp.message ?: "").show()
        }
    }

    fun sendMail(file: File){
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        if (!file.exists() || !file.canRead()) {
            return
        }
        val uri = Uri.fromFile(file)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
    }

    fun openFile(file: File) {
        val intent = Intent()
        intent.action = android.content.Intent.ACTION_VIEW
        val data = Uri.fromFile(file)
        val type = "*/*"
        intent.setDataAndType(data, type)
        startActivity(intent)
    }



    override fun deleteItem(mainDTO: MainDTO) {
        mainViewModel.mainDAO.deleteById(mainDTO.id?:0)
        var removeItem: MainDTO?=null
        mainViewModel.items.forEach {
            if(it.id==mainDTO.id)
                removeItem=it
        }
        mainViewModel.items.remove(removeItem)
        bindAdapter()
    }

    override fun deleteGroup(items: List<MainDTO>) {
        DialogHelper.materialDialog("Are you sure to delete all items in group?","Yes","No",
                MaterialDialog.SingleButtonCallback { dialog, which ->
                    val arrIds:IntArray=IntArray(items.count())
                    items.forEachIndexed { index, mainDTO ->
                        arrIds.set(index,mainDTO.id?:0)
                    }
                    mainViewModel.mainDAO.deleteByIds(arrIds)
                    mainViewModel.items.removeAll(items)
                    bindAdapter()
                }, MaterialDialog.SingleButtonCallback { dialog, which ->
            dialog.dismiss()
        },context!!).show()

    }

    fun bindAdapter(){
        listviewFragmentBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            this@ListViewFragment.adapter = SearchAdapter(this@ListViewFragment, getListExpandGroup())
            adapter = this@ListViewFragment.adapter
        }

    }

    override fun onItemClick(mainDTO: MainDTO) {
        activity?.startActivity<VehicleActivity>("object" to mainDTO)
        activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    override fun onTextChanged(text: String) {
        mainViewModel.keyword=text
                offset=0
                mainViewModel.items= mainViewModel.filterListSearch(isDesc,offset,if(isDateSorting) "a.timestamp" else "b.name") as ArrayList<MainDTO>
                bindAdapter()
    }

    override fun reload() {
        mainViewModel.initItems()
        bindAdapter()

    }

}

interface ListViewFragmentListener {
    fun deleteItem(mainDTO: MainDTO)
    fun deleteGroup(items: List<MainDTO>)
    fun onItemClick(mainDTO: MainDTO)
}
