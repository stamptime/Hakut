package com.example.hakut.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hakut.R

class GaleryAdapter(private val galeryList: ArrayList<Bitmap>):RecyclerView.Adapter<GaleryAdapter.GaleryHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GaleryAdapter.GaleryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.galery_item,parent,false)
        return GaleryHolder(view)
    }

    override fun onBindViewHolder(holder: GaleryAdapter.GaleryHolder, position: Int) {
        val current = galeryList[position]
        holder.itemImage.setImageBitmap(current)
    }

    override fun getItemCount(): Int {
        return galeryList.size
    }

    class GaleryHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemImage:ImageView = itemView.findViewById(R.id.imageViewGalery)
    }
}
