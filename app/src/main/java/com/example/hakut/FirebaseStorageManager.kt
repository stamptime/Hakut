package com.example.hakut

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseStorageManager {

    private val storage = FirebaseStorage.getInstance().reference

    fun uploadImageLogo(uri: Uri, id: String){
        storage.child("${id}/storelogo.png").putFile(uri)
    }

    fun getImageLogoReference(id:String): StorageReference {
        return storage.child("${id}/storelogo.png")
    }
}