package com.example.hakut

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseStorageManager {

    private val storage = FirebaseStorage.getInstance().reference
    private val db = Firebase.firestore

    fun uploadImageLogo(uri: Uri, id: String){
        storage.child("${id}/storelogo.png").putFile(uri)
    }

    fun uploadGaleryImage(uri:Uri, id: String){

        val path = uri.pathSegments
        val extension = path[path.size-2].split('/')[1]
        val filename = "${path[path.size-1]}.$extension"
        storage.child("${id}/galery/$filename").putFile(uri)
            .addOnSuccessListener {
                db.collection("stores").document(id).get()
                    .addOnSuccessListener {
                        result ->

                            val query = result.get("galery").toString().split(",").map { j -> j.trim()
                            .replace("[","")
                            .replace("]","")
                                .removePrefix("null")}.toMutableList()
                            query.add(filename)

                            db.collection("stores").document(id).update("galery",query)
                                .addOnFailureListener { error -> println("Ocurrio un error al actualizar galeria ${error.message}") }

                    }
                    .addOnFailureListener { error -> println("Ocurrio un error al obtener el campo galeria: ${error.message}") }
            }
            .addOnFailureListener{ error -> println("Ocurrio un error al agregar la imagen a la galeria: ${error.message}")}
    }

    fun getImageLogoReference(id:String): StorageReference {
        return storage.child("${id}/storelogo.png")
    }

    fun getGaleryImage(id: String, imageName: String):StorageReference{

        return storage.child("${id}/galery/${imageName}")
    }

}