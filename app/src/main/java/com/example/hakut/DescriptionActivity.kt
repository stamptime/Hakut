package com.example.hakut

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        db.collection("stores").get().addOnCompleteListener{
            if(it.isSuccessful){
                val storeList: ArrayList<Store> = arrayListOf()
                val localFile = File.createTempFile("tempImage","jpg")

                for(i in it.result!!.documents){
                    storeList.add(i.toObject(Store::class.java)!!)
                }

                binding.recyclerViewGaleria.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerViewGaleria.setHasFixedSize(true)

                val currentStore = storeList[position!!.toInt()]

                FirebaseStorageManager().getImageLogoReference(currentStore.idStore?:"").getFile(localFile)
                    .addOnFailureListener{error -> println("Ha ocurrido un error: ${error.message}")}
                    .addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

                        binding.recyclerViewGaleria.adapter = GaleryAdapter(arrayListOf(bitmap,
                                                                                        bitmap, bitmap, bitmap))
                        binding.imageViewLogo.setImageBitmap(bitmap)
                    }

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