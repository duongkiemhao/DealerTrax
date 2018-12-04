package com.siliconstack.dealertrax.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.model.FilterDialogModel
import kotlinx.android.synthetic.main.selection_list_item.view.*


class FilterListAdapter(var items1: ArrayList<FilterDialogModel>) : RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {
    var items= ArrayList<FilterDialogModel>()

    fun convert(){
        items1.forEach {
            items.add(it.clone())
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(items.count()==0){
            convert()
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.selection_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName?.text = items.get(position).name
        var itemModel = items[position]
        if (itemModel.isSelect) {
            holder.txtName.setTextColor(holder.itemView.resources.getColor(R.color.color_1))
            holder.imageView.setImageResource(R.drawable.ic_check_color1)
        } else {
            holder.txtName.setTextColor(holder.itemView.resources.getColor(R.color.color_4))
            holder.imageView.setImageResource(R.drawable.transparent)
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                items.forEachIndexed { index, it ->
                    if(it.isSelect){
                        it.isSelect=false
                        notifyItemChanged(index)
                    }
                }
                itemModel.isSelect = true
                notifyItemChanged(position)
            }
        })

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items1.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.txt_name
        val imageView = view.imageView
    }

}
