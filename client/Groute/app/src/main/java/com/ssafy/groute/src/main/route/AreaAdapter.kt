package com.ssafy.groute.src.main.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.groute.R
import com.ssafy.groute.src.main.home.Category

class AreaAdapter : RecyclerView.Adapter<AreaAdapter.AreaHolder>(){
    var list = mutableListOf<Category>()
    inner class AreaHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindInfo(data : Category){
            Glide.with(itemView)
                .load(data.img)
                .into(itemView.findViewById(R.id.route_iv_areaimg))

            itemView.findViewById<TextView>(R.id.route_tv_areaname).text = data.name

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_route_areaitem,parent,false)
        return AreaHolder(view)
    }

    override fun onBindViewHolder(holder: AreaHolder, position: Int) {
        holder.apply {
            bindInfo(list[position])
            itemView.setOnClickListener {
                itemClickListener.onClick(it, position, list[position].name)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, name: String)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}