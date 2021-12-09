package com.example.hakut

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.hakut.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

enum class MessageType{
    INVALID,
    ERROR,
    INCOMPLETE,
    DIFERENT
}


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup(){

        title = "Autenticacion"
        binding.registerButton.setOnClickListener {
            goRegister()
        }

        binding.buttonAnonimo.setOnClickListener {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener{
                if(it.isSuccessful)
                    goHome("",ProviderType.ANONIMO)
                else
                    showAlert(MessageType.ERROR)
            }
        }



//      Evento al presionar boton de login
        binding.loginButton.setOnClickListener {
            if(binding.editTextEmail.text.isNotEmpty() && binding.editTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            goHome(it.result?.user?.email?:"", ProviderType.BASIC)
                        }else{
                            showAlert(MessageType.ERROR)
                        }
                    }
            }else{

                showAlert(MessageType.INCOMPLETE)

            }
        }



    }

//    Alertas de error dependiendo la situacion
    private fun showAlert(type:MessageType){

        val builder = AlertDialog.Builder(this)
        builder.setTitle(type.name)

        val message = when(type){
            MessageType.ERROR -> "La autenticacion no se realizo con exito"
            MessageType.INCOMPLETE -> "Datos incompletos"
            else -> "Ocurrio algo inesperado"
        }

        builder.setMessage(message)
        builder.setPositiveButton("Continuar", null)
        builder.create().show()

    }


    /*FUNCIONES PARA CAMBIO DE VISTAS*/

    private fun goHome(email:String, provider: ProviderType){

        val intent:Intent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        binding.editTextEmail.text.clear()
        binding.editTextPassword.text.clear()
        startActivity(intent)

    }

    private fun goRegister(){
        binding.editTextEmail.text.clear()
        binding.editTextPassword.text.clear()
        val intent: Intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }

}