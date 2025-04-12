package com.example.casehive.Activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.R
import com.example.casehive.adapters.ViviendaAdapter
import com.example.casehive.models.Vivienda
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViviendaAdapter
    private val listaViviendas = mutableListOf<Vivienda>()

    private lateinit var searchEditText: EditText
    private lateinit var searchSpinner: Spinner
    private lateinit var searchButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViviendas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ViviendaAdapter(listaViviendas)
        recyclerView.adapter = adapter

        searchEditText = findViewById(R.id.searchEditText)
        searchSpinner = findViewById(R.id.searchSpinner)
        searchButton = findViewById(R.id.searchButton)

        val opciones = listOf("Tipo", "Precio", "Localidad")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        searchSpinner.adapter = spinnerAdapter

        searchButton.setOnClickListener {
            val criterio = searchSpinner.selectedItem.toString()
            val texto = searchEditText.text.toString().trim()

            val resultadosFiltrados = when (criterio) {
                "Tipo" -> listaViviendas.filter { it.tipo.contains(texto, ignoreCase = true) }
                "Precio" -> {
                    val precioBuscado = texto.toIntOrNull()
                    if (texto.isEmpty()) {
                        listaViviendas // 🔁 no se aplica ningún filtro si el campo está vacío
                    } else if (precioBuscado != null) {
                        listaViviendas.filter { kotlin.math.abs(it.precio - precioBuscado) <= 10000 }
                    } else {
                        emptyList()
                    }
                }
                "Localidad" -> listaViviendas.filter { it.ubicación.contains(texto, ignoreCase = true) }
                else -> listaViviendas
            }

            adapter = ViviendaAdapter(resultadosFiltrados)
            recyclerView.adapter = adapter
        }

        database = FirebaseDatabase.getInstance().reference
        cargarViviendas()

        val btnFavoritos = findViewById<Button>(R.id.btnFavoritos)
        btnFavoritos.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }

        val btnAgregar = findViewById<Button>(R.id.btnAgregarVivienda)
        btnAgregar.setOnClickListener {
            val intent = Intent(this, AgregarViviendaActivity::class.java)
            startActivity(intent)
        }

        val btnChats = findViewById<Button>(R.id.btnVerChats)
        btnChats.setOnClickListener {
            val intent = Intent(this, MisChatsActivity::class.java)
            startActivity(intent)
        }
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CALL_PHONE), 123)
        }

    }

    private fun cargarViviendas() {
        val viviendasRef = database.child("viviendas")
        viviendasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaViviendas.clear()
                for (item in snapshot.children) {
                    val vivienda = item.getValue(Vivienda::class.java)
                    vivienda?.let { listaViviendas.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar viviendas: ${error.message}")
            }
        })
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso para llamadas concedido", Toast.LENGTH_SHORT).show()
        }
    }
}
