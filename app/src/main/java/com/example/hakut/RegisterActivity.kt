package com.example.hakut

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import com.example.hakut.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
        binding.buttonContinue.setOnClickListener {
            registerUser()

        }
    }

    private fun setUp(){
       title = "Crea tu usuario"
    }

    private fun registerUser(){

        var empty = true
        val editTexts:List<EditText> = listOf(
            binding.editTextName,
            binding.editTextEmail,

//          No cambiar las ultimas dos posiciones
            binding.editTextPassword,
            binding.editTextConfirm
        )

        for(i in editTexts) {
            if (i.text.isEmpty()) {
                empty = true
                break
            }
            empty = false
        }

        if(!empty){
            if(editTexts[editTexts.lastIndex].text.toString() == editTexts[editTexts.lastIndex-1].text.toString()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            createData(it.result?.user?.uid?:"",binding.editTextName.text.toString(), it.result?.user?.email?.toString()?:"")
                            goRegisterStore(it.result?.user?.email?:"",binding.editTextPassword.text.toString())
                        }else{
                            showAlert(MessageType.ERROR)
                        }
                    }
            }else{
                showAlert(MessageType.DIFERENT)
            }
        }else{
            showAlert(MessageType.INCOMPLETE)
        }

    }

    private fun createData(id:String, name:String,email:String){
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )
        db.collection("users").document(id).set(user)
            .addOnSuccessListener { x -> Log.d(TAG,"Mensaje exitoso: ${x}") }
            .addOnFailureListener{ x -> Log.w(TAG, "Mensaje de error: ",x)}
    }

    private fun showAlert(type: MessageType){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(type.name)
        builder.setMessage( when(type){
            MessageType.ERROR -> "Error en la autenticacion"
            MessageType.INCOMPLETE -> "Datos incompletos"
            MessageType.DIFERENT -> "Las contrasenas no coinciden"
            else -> "Ha ocurrido un error inesperado"
        })
        builder.setPositiveButton("Aceptar",null)
        builder.create().show()
    }

    private fun goRegisterStore(email:String,pass:String){
        val intent: Intent = Intent(this, RegisterStoreActivity::class.java).apply {
            putExtra("email",email)
            putExtra("pass",pass)
        }
        startActivity(intent)
    }

}