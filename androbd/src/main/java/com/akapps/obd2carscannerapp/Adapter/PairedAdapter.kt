package com.akapps.obd2carscannerapp.Adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.akapps.obd2carscannerapp.R

class PairedAdapter(val context: Context,val bluetoothlist: ArrayList<BluetoothDevice>): RecyclerView.Adapter<PairedAdapter.MyViewHolder>() {
    var itemclickInterface: ItemClickInterface?=null
    fun setItemClickListner(itemInterface: ItemClickInterface){
        this.itemclickInterface= itemInterface
    }
    class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val maincontraing= itemview.findViewById<ConstraintLayout>(R.id.maincontraing)
        val bluetoothname= itemview.findViewById<TextView>(R.id.bluetoothname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val view= LayoutInflater.from(context).inflate(R.layout.bluetooth_item,parent,false)
        return  MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bluetoothlist.size
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bluetoothname.text= bluetoothlist.get(position).name

        holder.maincontraing.setOnClickListener {
            itemclickInterface!!.onItemClick(position)
        }
    }
    interface ItemClickInterface{
        fun onItemClick(position: Int)
    }
}