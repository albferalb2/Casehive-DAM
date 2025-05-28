package com.example.casehive.Activitys.Chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.R
import com.example.casehive.adapters.MensajeAdapter
import com.example.casehive.models.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var mensajesRef: DatabaseReference
    private lateinit var mensajeEditText: EditText
    private lateinit var enviarButton: Button
    private lateinit var recyclerMensajes: RecyclerView
    private lateinit var adapter: MensajeAdapter

    private val listaMensajes = mutableListOf<Mensaje>()
    private lateinit var remitenteId: String
    private lateinit var receptorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        remitenteId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        receptorId = intent.getStringExtra("receptorId") ?: return

        mensajeEditText = findViewById(R.id.mensajeEditText)
        enviarButton = findViewById(R.id.enviarButton)
        recyclerMensajes = findViewById(R.id.recyclerMensajes)
        recyclerMensajes.layoutManager = LinearLayoutManager(this)
        adapter = MensajeAdapter(listaMensajes, remitenteId)
        recyclerMensajes.adapter = adapter

        mensajesRef = FirebaseDatabase.getInstance().getReference("chats")

        enviarButton.setOnClickListener {
            val texto = mensajeEditText.text.toString().trim()
            if (texto.isNotEmpty()) {
                val timestamp = System.currentTimeMillis()
                val mensaje = Mensaje(texto, timestamp)

                val ruta1 = mensajesRef.child(remitenteId).child(receptorId).child("msg_$timestamp")
                val ruta2 = mensajesRef.child(receptorId).child(remitenteId).child("msg_$timestamp")

                ruta1.setValue(mensaje)
                ruta2.setValue(mensaje)

                mensajeEditText.setText("")
            }
        }

        escucharMensajes()
    }

    private fun escucharMensajes() {
        mensajesRef.child(remitenteId).child(receptorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMensajes.clear()
                    for (msgSnapshot in snapshot.children) {
                        val mensaje = msgSnapshot.getValue(Mensaje::class.java)
                        mensaje?.let { listaMensajes.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerMensajes.scrollToPosition(listaMensajes.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, "Error al cargar mensajes", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
