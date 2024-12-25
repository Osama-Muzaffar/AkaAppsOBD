//package com.mumbo.obdiiscannerassistant.Adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.mumbo.obdiiscannerassistant.R
//import com.mumbo.obdiiscannerassistant.databinding.SliderItemRvBinding
//import com.smarteist.autoimageslider.SliderViewAdapter
//
//class SliderAdapter(val context: Context,val mSliderItems: ArrayList<Int>): SliderViewAdapter<SliderAdapter.MyViewHolder>() {
//    class MyViewHolder(itemView: View): SliderViewAdapter.ViewHolder(itemView) {
//        val binding= SliderItemRvBinding.bind(itemView)
//    }
//
//    override fun getCount(): Int {
//        return mSliderItems.size
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
//        val view= LayoutInflater.from(context).inflate(R.layout.slider_item_rv,parent,false)
//        return MyViewHolder(view)
//    }
//
//    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
//        viewHolder!!.binding.image.setImageResource(mSliderItems.get(position))
//    }
//}