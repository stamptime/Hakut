package com.example.hakut

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hakut.adapters.GaleryAdapter
import com.example.hakut.databinding.ActivityDescriptionBinding
import com.example.hakut.model.Store
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import com.google.firebase.storage.FirebaseStorage as FirebaseStorage

class DescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescriptionBinding
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        hakum-f7562.appspot.com
        val position = intent.extras!!.getString("position")
        binding.recyclerViewGaleria.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewGaleria.setHasFixedSize(true)
        //Getting the stores data from the database
        db.collection("stores").get().addOnCompleteListener{

            if(it.isSuccessful){

                val storeList: ArrayList<Store> = arrayListOf()
                val localFile = File.createTempFile("tempImage","jpg")

                for(i in it.result!!.documents){
                    storeList.add(i.toObject(Store::class.java)!!)
                }

                val currentStore = storeList[position!!.toInt()]

                //Setting the main image
                FirebaseStorageManager().getImageLogoReference(currentStore.idStore?:"").getFile(localFile)
                    .addOnFailureListener{error -> println("Ha ocurrido un error al buscar la imagen: ${error.message}")}
                    .addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.imageViewLogo.setImageBitmap(bitmap)
                    }

                val bitmapArray : ArrayList<Bitmap> = arrayListOf()

                db.collection("stores").document(currentStore.idStore?:"").get()
                    .addOnSuccessListener { x ->
                        if(x.get("galery").toString() !== "null"){
                            val arr = x.get("galery").toString().split(",").map { j -> j.trim()
                                .replace("[","")
                                .replace("]","") }

                            for(i in arr){
                                FirebaseStorageManager().getGaleryImage(currentStore.idStore?:"",i).getFile(localFile)
                                    .addOnFailureListener{error -> println("Ha ocurrido un error al buscar la imagen: ${error.message}")}
                                println("NOt handle")
                                Handler().postDelayed({
                                    bitmapArray.add(BitmapFactory.decodeFile(localFile.absolutePath))
                                    println("Handler")
                                },1000)
                            }
                            Handler().postDelayed({binding.recyclerViewGaleria.adapter = GaleryAdapter(bitmapArray)},2000)

                        }
                    }
                    .addOnFailureListener { x -> println("No se ha encontrado ninguna galeria") }

                binding.textViewTitle.text = currentStore.name
                binding.textViewDescripcion.text = currentStore.description?:"No hay descripcion"

            }else{
                println("Ha sucedido un error al consultar datos: " + it.exception!!.message)
            }
        }
    }

    private fun setRecyclerView(){

    }

}