package com.example.hakut

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hakut.databinding.ActivityHomeBinding
import com.example.hakut.model.Store
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.FieldPosition
import java.util.*

enum class ProviderType{
    BASIC,
    ANONIMO
}

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()

        //Verificando parametros de la actividad anterior
        println(intent.extras?.getString("provider"))
        if(intent.extras?.getString("provider") == ProviderType.ANONIMO.name)
            binding.buttonLogOut.text = "cerrar"

        binding.buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut().also { goAuth() }
            onBackPressed().also { goAuth() }
        }

    }

    private fun setUp(){

        val recyclerView = binding.storeList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)



        /* Mostrando las tiendas en el homeActivity*/
        db.collection("stores").get().addOnSuccessListener { resultado ->
            val storeList = arrayListOf<Store>()
            for(i in resultado.documents){
                storeList.add(i.toObject(Store::class.java)!!)
            }
            println(storeList)
            val myAdapter = StoreAdapter(storeList)
            recyclerView.adapter = myAdapter
            myAdapter.setOnItemClickListener(object: StoreAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    goDescription(position)
                }
            })
        }
    }

    private fun goAuth(){
        val intent: Intent = Intent(this,AuthActivity::class.java)
        startActivity(intent)
    }

    private fun goDescription(position: Int){

        intent = Intent(this,DescriptionActivity::class.java).apply {
            putExtra("position", position.toString())
        }
        startActivity(intent)
    }
}