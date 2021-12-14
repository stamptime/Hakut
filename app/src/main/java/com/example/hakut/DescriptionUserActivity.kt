package com.example.hakut

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hakut.adapters.GaleryAdapter
import com.example.hakut.databinding.ActivityDescriptionUserBinding
import com.example.hakut.model.Store
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class DescriptionUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescriptionUserBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewGaleria.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewGaleria.setHasFixedSize(true)

        setUp()
    }

    private fun setUp(){

        val position = intent.extras!!.getString("position")

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
                                    .addOnSuccessListener {
                                        bitmapArray.add(BitmapFactory.decodeFile(localFile.absolutePath))
                                        binding.recyclerViewGaleria.adapter = GaleryAdapter(bitmapArray)
                                    }
                            }


                        }
                    }
                    .addOnFailureListener { x -> println("No se ha encontrado ninguna galeria") }

                binding.editTextTextTitle.setText(currentStore.name)
                binding.editTextDescription.setText(currentStore.description?:"No hay descripcion")

            }else{
                println("Ha sucedido un error al consultar datos: " + it.exception!!.message)
            }
        }

    }
}