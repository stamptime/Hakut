package com.example.hakut

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.hakut.databinding.ActivityRegisterStoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.Manifest
import androidx.core.content.ContextCompat

class RegisterStoreActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterStoreBinding
    private val db = Firebase.firestore
    private lateinit var imageURI: Uri
    private var imageEmpty = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
        binding.buttonUploadImage.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permision = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permision,100)
                }else{
                    uploadImage()
                }
            }else{
                uploadImage()
            }

        }

        binding.buttonFinish.setOnClickListener {
            validateData()
        }
    }

    private fun uploadImage() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 100)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            imageURI = data?.data!!
            binding.imageView.setImageURI(imageURI)
            imageEmpty = false
        }
    }


    private fun setUp(){
        title = "Crear tienda"
    }

    private fun validateData(){
        var empty = false;

        val inputList: List<EditText> = listOf(
            binding.editTextName,
            binding.editTextType,
            binding.editTextSite
        )
        for(i in inputList){
            if(i.text.isEmpty()){
                empty = true
                break
            }
        }

        if(!empty){
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                    intent.extras?.getString("email")?:"",
                    intent.extras?.getString("pass")?:"")
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        createData(FirebaseAuth.getInstance().uid.toString(),
                            binding.editTextName.text.toString(), binding.editTextType.text.toString(),
                            binding.editTextSite.text.toString())
                    }else{
                        showAlert(MessageType.ERROR)
                    }
                }


        }else{
            showAlert(MessageType.INCOMPLETE)
        }
    }

    private fun createData(uid:String,storeName:String,storeType:String, storeURL:String){
        val storeData = hashMapOf(
            "idStore" to null,
            "uid" to uid,
            "name" to storeName,
            "type" to storeType,
            "url" to storeURL,
        )

        db.collection("stores").add(storeData)
            .addOnFailureListener { x -> println("Ha ocurrido un error en la tienda: $x")}
            .addOnSuccessListener { element ->
                db.collection("stores").document(element.id).update("idStore", element.id)
                    .addOnFailureListener { error -> println("Ha sucedido un error guardando el id de la tienda: $error") }
                    .addOnSuccessListener { println("Id guardado exitosamente") }

                uploadToFirestore(element.id)
                goHome()
            }
    }

    private fun uploadToFirestore(id:String){
        if(!imageEmpty)
            FirebaseStorageManager().uploadImageLogo(imageURI,id)

    }

    private fun showAlert(type: MessageType){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(type.name)
        builder.setPositiveButton("Continuar",null)
        builder.setMessage(when(type){
            MessageType.ERROR -> "La autenticacion no se realizo con exito"
            MessageType.INCOMPLETE -> "Datos incompletos"
            else -> "Ocurrio algo inesperado"
        })
        builder.create().show()
    }

    private fun showAlert(title:String, message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setPositiveButton("Continuar",null)
        builder.setMessage(message)
        builder.create().show()
    }

    private fun goHome(){
        val intent: Intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

}