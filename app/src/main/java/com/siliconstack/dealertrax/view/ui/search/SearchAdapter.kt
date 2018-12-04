package com.siliconstack.dealertrax.view.ui.search

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.databinding.SearchHeaderItemBinding
import com.siliconstack.dealertrax.databinding.SearchItemBinding
import com.siliconstack.dealertrax.model.MainDTO
import com.siliconstack.dealertrax.view.utility.DateUtility
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import java.util.*


class SearchAdapter(val searchListener: ListViewFragmentListener, groups: List<ExpandableGroup<*>>) : ExpandableRecyclerViewAdapter<SearchAdapter.MyGroupViewHolder, SearchAdapter.MyChildViewHolder>(groups) {
    override fun onBindChildViewHolder(viewHolder: MyChildViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {

        var item = group!!.items.get(childIndex) as MainDTO

        viewHolder!!.binding.txtValue.setText(item.scanText)

        viewHolder.binding.txtLocation.setText(item.locationName)
        viewHolder.binding.txtFloor.setText(item.floorName)
        viewHolder.binding.txtBay.setText(item.bayNumber)
        viewHolder.binding.txtName.setText(item.operatorName)
        val dateTime = Date(item.timestamp ?: 0)
        viewHolder.binding.txtTime.setText(DateUtility.parseDateToDateTimeStr("dd/MM/yyyy",Date(item.timestamp?:0)))

        viewHolder.binding.btnDelete.setOnClickListener {
            searchListener.deleteItem(item)

        }
        if (item.isSelected)
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.color_5))
        else viewHolder.itemView.setBackgroundColor(Color.WHITE)
        viewHolder.binding.swipeLayout.surfaceView.setOnClickListener {
            searchListener.onItemClick(item)
        }
        viewHolder.binding.txtCompare.text=item.compareTime

    }

    override fun onBindGroupViewHolder(holder: MyGroupViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder!!.binding.txtHeader.text = group!!.title + " (" + group.itemCount + ")"
        holder.binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            group.items.forEach {
                val item = it as MainDTO
                item.isSelected = isChecked
            }

            notifyDataSetChanged()
        }
        holder.binding.btnDelete.setOnClickListener {
            searchListener.deleteGroup(group.items as List<MainDTO>)

        }


    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): MyGroupViewHolder {
        var binding: ViewDataBinding = DataBindingUtil.inflate<SearchHeaderItemBinding>(LayoutInflater.from(parent!!.context), R.layout.search_header_item,
                parent, false)
        return MyGroupViewHolder(binding as SearchHeaderItemBinding)

    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): MyChildViewHolder {
        var binding: ViewDataBinding = DataBindingUtil.inflate<SearchItemBinding>(LayoutInflater.from(parent!!.context), R.layout.search_item,
                parent, false)
        return MyChildViewHolder(binding as SearchItemBinding)

    }


    class MyChildViewHolder(val binding: SearchItemBinding) : ChildViewHolder(binding.root)

    class MyGroupViewHolder(val binding: SearchHeaderItemBinding) : GroupViewHolder(binding.root)

}


