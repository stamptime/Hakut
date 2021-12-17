package com.example.hakut

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Adapter
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hakut.databinding.ActivityHomeBinding
import com.example.hakut.model.Store
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.protobuf.NullValue
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

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
            val myAdapter = StoreAdapter(storeList)

//            binding.searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    TODO("Not implemented")
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    if (newText!!.isNotEmpty()) {
////                    storeList = storeList.map { x -> if(x.name!!.lowercase().contains(newText.lowercase())) x else null } as ArrayList<Store>
//                    }
//                    return true
//                }
//            })


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
        val storesList = arrayListOf<Store>()
        db.collection("stores").get().addOnCompleteListener{

            if(it.isSuccessful){
                for( i in it.result!!.documents){
                    storesList.add(i.toObject(Store::class.java)!!)
                }

                if(storesList[position].uid == FirebaseAuth.getInstance().currentUser!!.uid){
                    intent = Intent(this,DescriptionUserActivity::class.java).apply {
                        putExtra("position", position.toString())
                        putExtra("idStore", storesList[position].idStore)
                    }
                }else{
                    intent = Intent(this,DescriptionActivity::class.java).apply {
                        putExtra("position", position.toString())
                    }
                }
            }

            startActivity(intent)
        }

    }
}