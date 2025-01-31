package com.akapps.obd2carscannerapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akapps.obd2carscannerapp.Activity.FaultCodeDetailsActivity
import com.akapps.obd2carscannerapp.Database.ObdEntry
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.FaultRvItemBinding

class FaultRvAdapter(val context: Context,  faultList: ArrayList<ObdEntry>): RecyclerView.Adapter<FaultRvAdapter.MyViewHolder>() {
    //private val itemListFull: List<ObdEntry> = faultList.toList()  // Copy of the full list for filtering

    private var faultList: ArrayList<ObdEntry> = faultList
    private var itemListFull: ArrayList<ObdEntry> = ArrayList(faultList)

    class  MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding= FaultRvItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.fault_rv_item,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return faultList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.titletxt.setText(faultList.get(position).name)

        holder.binding.mainrelative.setOnClickListener {
            context.startActivity(Intent(context, FaultCodeDetailsActivity::class.java)
                .putExtra("code",faultList.get(position).code))
        }
    }


    fun filter(query: String) {
        faultList = if (query.isEmpty()) {
            //itemListFull as ArrayList<ObdEntry>
            ArrayList(itemListFull)
        } else {
            itemListFull.filter { it.name!!.contains(query, ignoreCase = true) } as ArrayList<ObdEntry>
        }
        notifyDataSetChanged()
    }
    fun updateData(newEntries: List<ObdEntry>) {
        faultList.clear()
        faultList.addAll(newEntries)

        itemListFull.clear() // Update the backup list
        itemListFull.addAll(newEntries)
        notifyDataSetChanged()
    }
}