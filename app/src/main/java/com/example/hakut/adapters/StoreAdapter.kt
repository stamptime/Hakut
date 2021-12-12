package com.example.hakut

import android.graphics.BitmapFactory
import android.location.GnssAntennaInfo
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.hakut.model.Store
import java.io.File

class StoreAdapter(private val storeList: ArrayList<Store>) : RecyclerView.Adapter<StoreAdapter.StoreHolder>() {

    private lateinit var myListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position:Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.store_item,parent,false)
        return StoreHolder(view,myListener)
    }

    override fun onBindViewHolder(holder: StoreHolder, position: Int) {

        val localFile = File.createTempFile("tempImage","jpg")

        val current = storeList[position]
        holder.name.text = current.name
        holder.type.text = current.type
        holder.url.text = current.url

        FirebaseStorageManager().getImageLogoReference(current.idStore?:"").getFile(localFile)
            .addOnFailureListener{error -> println("Ha ocurrido un error: ${error.message}")}
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.imageView.setImageBitmap(bitmap)
            }
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class StoreHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val name:TextView = itemView.findViewById(R.id.textViewName)
        val type:TextView = itemView.findViewById(R.id.textViewType)
        val url: TextView = itemView.findViewById(R.id.textViewUrl)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewLogo)

        init {

            itemView.setOnClickListener {

                listener.onItemClick(bindingAdapterPosition)

            }

        }
    }
}
