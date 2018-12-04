package com.siliconstack.dealertrax.view.ui.setting


import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.databinding.SettingItemBinding
import com.siliconstack.dealertrax.model.SettingDTO


class SettingAdapter(var settingListener: SettingListener) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    var items: List<SettingDTO> = emptyList()

    enum class SettingEnum{
        LOCATION,FLOOR,NAME
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<SettingItemBinding>(LayoutInflater.from(parent.context), R.layout.setting_item,
                parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item= this.items[position]
        holder.binding.txtName.text=item .name
        holder.binding.btnDelete.setOnClickListener {
            settingListener.onRemove(item)
        }

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val binding: SettingItemBinding) : RecyclerView.ViewHolder(binding.root)

}
