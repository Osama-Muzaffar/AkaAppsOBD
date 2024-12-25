package com.akapps.obd2carscannerapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akapps.obd2carscannerapp.Models.PurchaseModel
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.PurchaseItemBinding

class PurchaseAdapter(val context: Context, val list: List<PurchaseModel>,
                      val purchaseInteface: PurchaseInteface): RecyclerView.Adapter<PurchaseAdapter.MyViewHolder>() {
    interface PurchaseInteface{
        fun onPurchaseClick(sku: String)
    }
    class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
    val binding= PurchaseItemBinding.bind(itemview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.purchase_item,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var title = list.get(position).skuDetails.title
        val modifiedtitle = title.replace("(OBD2 Bluetooth Car Scanner ELM)", "")
        holder.binding.title.text= modifiedtitle
//        if(position==2){
//            holder.binding.price.text= list.get(position).skuDetails.price+" / Lifetime"
//        }
//        else{
//            holder.binding.price.text= list.get(position).skuDetails.price+" / Per Month"
//        }
        holder.binding.price.text= list.get(position).skuDetails.price


        holder.binding.description.text= list.get(position).skuDetails.description

        if(list.get(position).ischecked){
            holder.binding.checkimg.visibility = View.VISIBLE
        }
        else{
            holder.binding.checkimg.visibility = View.GONE
        }

        holder.binding.maincontraint.setOnClickListener {
            holder.binding.checkrelative.performClick()
        }

        holder.binding.checkrelative.setOnClickListener {
            Log.d("PurchaseAdapter", "on check click: position = $position ")
            for (i in 0 until list.size){
                Log.d("PurchaseAdapter", "on check click: i = $i ")

                if (position == i){
                    list.get(i).ischecked = true
                    purchaseInteface.onPurchaseClick(list.get(i).skuDetails.sku)

                }
                else{
                    list.get(i).ischecked = false
                }
            }

            notifyDataSetChanged()
        }
    }
    fun getPurchaseList(): List<PurchaseModel>{
        return list
    }
}