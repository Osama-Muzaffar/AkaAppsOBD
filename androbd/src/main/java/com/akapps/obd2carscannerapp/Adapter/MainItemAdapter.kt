package com.akapps.obd2carscannerapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akapps.obd2carscannerapp.Models.MainItemModel
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.MainRvItemBinding

class MainItemAdapter(val context: Context, val listItem: ArrayList<MainItemModel>,
                      val onItemClick: MainItemInterface
): RecyclerView.Adapter<MainItemAdapter.MyViewHolder>() {



    class MyViewHolder(itemview: View):  RecyclerView.ViewHolder(itemview) {
        val adapterbinding= MainRvItemBinding.bind(itemview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.main_rv_item,parent,false)
        return  MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return   listItem.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.adapterbinding.mainimg.setImageResource(listItem.get(position).resId)
        holder.adapterbinding.maintxt.setText(listItem.get(position).name)
        if (!listItem.get(position).isenable){
            if (position>=0 && position<3 ){
                holder.adapterbinding.maintxt.alpha= 1f
                holder.adapterbinding.mainimg.alpha= 1f
            }
            else{
                holder.adapterbinding.maintxt.alpha= 0.2f
                holder.adapterbinding.mainimg.alpha= 0.2f
            }
        }
        else{
            if (position==0){
                holder.adapterbinding.maintxt.alpha= 0.2f
                holder.adapterbinding.mainimg.alpha= 0.2f
            }
            else{
                holder.adapterbinding.maintxt.alpha= 1f
                holder.adapterbinding.mainimg.alpha= 1f
            }

        }

        holder.adapterbinding.maincontraint.setOnClickListener {
            if (position>=0 && position<3 ){
                onItemClick.onMainItemClick(position)
            }
            else {
                if (listItem.get(position).isenable) {
                    onItemClick.onMainItemClick(position)
                }
            }
        }

    }


    fun setEnableItems(){
        for(item in listItem){
            item.isenable= true
        }
        notifyDataSetChanged()
    }
    fun setDisableItems(){
        for(item in listItem){
            item.isenable= false
        }
        notifyDataSetChanged()
    }

    interface MainItemInterface{
        fun onMainItemClick(position: Int)
    }

}