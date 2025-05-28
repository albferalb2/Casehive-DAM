package com.example.casehive.Activitys.Viviendas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.Activitys.Chats.MisChatsActivity
import com.example.casehive.Activitys.MainActivity
import com.example.casehive.Activitys.Viviendas.Editar_Agregar.EditarViviendaActivity
import com.example.casehive.R
import com.example.casehive.adapters.MisViviendasAdapter
import com.example.casehive.models.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MisViviendasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MisViviendasAdapter
    private val listaViviendas = mutableListOf<Vivienda>()
    private lateinit var dbRef: DatabaseReference
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private lateinit var  btnfavo: Button
    private lateinit var btnmain:Button
    private lateinit var btnchats:Button
    private lateinit var btnmisviv:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_viviendas)

        recyclerView = findViewById(R.id.recyclerMisViviendas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MisViviendasAdapter(listaViviendas,
            onEditar = { vivienda ->
                val intent = Intent(this, EditarViviendaActivity::class.java)
                intent.putExtra("viviendaId", vivienda.id)
                startActivity(intent)
            },
            onEliminar = { vivienda ->
                dbRef.child(vivienda.id).removeValue().addOnSuccessListener {
                    listaViviendas.remove(vivienda)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Vivienda eliminada", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("viviendas")
        btnfavo = findViewById(R.id.btnFavoritos)
        btnfavo.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        btnmain=findViewById(R.id.btnViviendas)
        btnmain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnchats=findViewById(R.id.btnVerChats)
        btnchats.setOnClickListener {
            startActivity(Intent(this, MisChatsActivity::class.java))
        }
        btnmisviv=findViewById(R.id.btnMisViviendas)
        btnmisviv.setOnClickListener {
            startActivity(Intent(this, MisViviendasActivity::class.java))
        }
        cargarMisViviendas()
    }

    private fun cargarMisViviendas() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaViviendas.clear()
                for (data in snapshot.children) {
                    try {
                        val viviendaMap = data.value as Map<*, *>
                        val vivienda = Vivienda(
                            tipo = viviendaMap["tipo"] as? String ?: "",
                            ubicación = viviendaMap["ubicación"] as? String ?: "",
                            descripción = viviendaMap["descripción"] as? String ?: "",
                            habitaciones = (viviendaMap["habitaciones"] as? Long)?.toInt() ?: 0,
                            baños = (viviendaMap["baños"] as? Long)?.toInt() ?: 0,
                            superficie_m2 = (viviendaMap["superficie_m2"] as? Long)?.toInt() ?: 0,
                            plantas = (viviendaMap["plantas"] as? Long)?.toInt() ?: 1,
                            precio = (viviendaMap["precio"] as? Long)?.toInt() ?: 0,
                            año_construcción = (viviendaMap["año_construcción"] as? Long)?.toInt() ?: 0,
                            estado = viviendaMap["estado"] as? String ?: "",
                            certificación_energética = viviendaMap["certificación_energética"] as? String ?: "",
                            extras = when (val raw = viviendaMap["extras"]) {
                                is List<*> -> raw.filterIsInstance<String>()
                                is Map<*, *> -> raw.values.filterIsInstance<String>()
                                else -> emptyList()
                            },
                            imágenes = when (val raw = viviendaMap["imágenes"]) {
                                is List<*> -> raw.filterIsInstance<String>()
                                is Map<*, *> -> raw.values.filterIsInstance<String>()
                                else -> emptyList()
                            },
                            creadorId = viviendaMap["creadorId"] as? String ?: ""
                        )
                        vivienda.id = data.key ?: ""
                        if (vivienda.creadorId == userId) {
                            listaViviendas.add(vivienda)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MisViviendasActivity, "Error al leer vivienda", Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MisViviendasActivity, "Error al cargar", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
