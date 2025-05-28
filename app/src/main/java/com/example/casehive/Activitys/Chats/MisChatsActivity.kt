package com.example.casehive.Activitys.Chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.Activitys.MainActivity
import com.example.casehive.Activitys.Viviendas.FavoritosActivity
import com.example.casehive.Activitys.Viviendas.MisViviendasActivity
import com.example.casehive.R
import com.example.casehive.adapters.MisChatsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MisChatsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MisChatsAdapter
    private val listaConversaciones = mutableListOf<String>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_chats)

        recyclerView = findViewById(R.id.recyclerMisChats)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MisChatsAdapter(listaConversaciones) { receptorId ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receptorId", receptorId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        cargarConversaciones()

        findViewById<Button>(R.id.btnViviendas).setOnClickListener {
           startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.btnFavoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerChats).setOnClickListener {
            startActivity(Intent(this, MisChatsActivity::class.java))
        }
        findViewById<Button>(R.id.btnMisViviendas).setOnClickListener {
            startActivity(Intent(this, MisViviendasActivity::class.java))
        }
    }

    private fun cargarConversaciones() {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatsRef = database.child("chats")

        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaConversaciones.clear()

                for (usuarioSnap in snapshot.children) {
                    val usuarioId = usuarioSnap.key ?: continue

                    if (usuarioId == currentUserId) {
                        for (receptorSnap in usuarioSnap.children) {
                            val receptorId = receptorSnap.key ?: continue
                            if (!listaConversaciones.contains(receptorId)) {
                                listaConversaciones.add(receptorId)
                            }
                        }
                    }

                    if (usuarioSnap.hasChild(currentUserId)) {
                        if (!listaConversaciones.contains(usuarioId)) {
                            listaConversaciones.add(usuarioId)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MisChatsActivity, "Error al cargar chats", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }
}
