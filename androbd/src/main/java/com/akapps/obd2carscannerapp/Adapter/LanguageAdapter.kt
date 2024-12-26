package com.akapps.obd2carscannerapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akapps.obd2carscannerapp.Models.LanguageModel
import com.akapps.obd2carscannerapp.Models.SharedPreferencesHelper
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.LanguageRvItemBinding
import com.murgupluoglu.flagkit.FlagKit

class LanguageAdapter(val context: Context,val  languageList: ArrayList<LanguageModel>,
                     val onSelect: (LanguageModel)-> Unit): RecyclerView.Adapter<LanguageAdapter.MyViewHolder>() {

                         val pref= SharedPreferencesHelper(context)
    var checkable = true
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding= LanguageRvItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.language_rv_item,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
      return languageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val peflngtest = pref.getString(SharedPreferencesHelper.KEY_LANGUAGE, "en")
        val currentcode = languageList.get(position).lngCode

        Log.d("LanguageAdapter", "checkable: $checkable")
        Log.d("LanguageAdapter", "peflngtest: $peflngtest")
        Log.d("LanguageAdapter", "currentcode: $currentcode")
        if(checkable) {
            val peflng = pref.getString(SharedPreferencesHelper.KEY_LANGUAGE, "en")
            if (languageList.get(position).lngCode.equals(peflng)) {
                languageList.get(position).isselect = true
            }
            else{
                languageList.get(position).isselect = false
            }
        }
        if(languageList.get(position).isselect){
            holder.binding.radiobtn.isChecked=true
        }
        else{
            holder.binding.radiobtn.isChecked=false
        }
        val resourceId = FlagKit.getResId(context, languageList.get(position).imagelink)
        holder.binding.flagimg.setImageResource(resourceId)

        holder.binding.title.setText(languageList.get(position).title)

        holder.binding.mainlinear.setOnClickListener {
            checkable=false
           for (lng in languageList){
               lng.isselect=false
           }
            languageList.get(position).isselect=true

            onSelect(languageList.get(position))
            notifyDataSetChanged()
        }
    }
}