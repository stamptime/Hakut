package com.example.hakut

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hakut.adapters.GaleryAdapter
import com.example.hakut.databinding.ActivityDescriptionUserBinding
import com.example.hakut.model.Store
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class DescriptionUserActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDescriptionUserBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewGaleria.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewGaleria.setHasFixedSize(true)

        binding.buttonAddImage.setOnClickListener {
            getImage()
        }

        binding.buttonSaveChanges.setOnClickListener {
            getData()
        }
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
        }.addOnFailureListener {  error -> println("Ha ocurrido un error al consultar los datos de la tienda: ${error.message}")}

    }

    private fun uploadData(field:String,value:String){
        db.collection("stores").document(intent.extras!!.getString("idStore")!!).update(field,value)
            .addOnFailureListener { error -> println("No se actualizaron los campos con exito: ${error.message}") }
    }

    private fun getData(){
        uploadData("name",binding.editTextTextTitle.text.toString())
        uploadData("description",binding.editTextDescription.text.toString())
    }

    private fun getImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent,200)
    }

    private fun uploadImage(imageUri:Uri){
        FirebaseStorageManager().uploadGaleryImage(imageUri,intent.extras!!.getString("idStore")!!)
    }
















    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK && requestCode == 200){
            uploadImage(data?.data!!)
        }
    }

}