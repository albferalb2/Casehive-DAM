package com.example.casehive.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.R
import com.example.casehive.adapters.ViviendaAdapter
import com.example.casehive.models.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoritosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViviendaAdapter
    private val listaFavoritos = mutableListOf<Vivienda>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        recyclerView = findViewById(R.id.recyclerFavoritos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ViviendaAdapter(listaFavoritos)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        cargarFavoritos()
        findViewById<Button>(R.id.btnViviendas).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnVerChats).setOnClickListener {
           startActivity(Intent(this, MisChatsActivity::class.java))
                 }


    }

    private fun cargarFavoritos() {
        val currentUserId = auth.currentUser?.uid ?: return
        val favsRef = database.child("favoritos").child(currentUserId)

        favsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaFavoritos.clear()
                val viviendaIds = snapshot.children.mapNotNull { it.key }

                if (viviendaIds.isEmpty()) {
                    adapter.notifyDataSetChanged()
                    return
                }

                val viviendasRef = database.child("viviendas")
                viviendasRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (viviendaSnap in dataSnapshot.children) {
                            val vivienda = viviendaSnap.getValue(Vivienda::class.java)
                            if (vivienda != null && viviendaSnap.key in viviendaIds) {
                                listaFavoritos.add(vivienda)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Error al cargar viviendas: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar favoritos: ${error.message}")
            }
        })
    }
}
