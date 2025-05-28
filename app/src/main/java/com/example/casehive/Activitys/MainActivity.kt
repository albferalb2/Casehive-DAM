package com.example.casehive.Activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.Activitys.Chats.MisChatsActivity
import com.example.casehive.Activitys.Viviendas.Editar_Agregar.AgregarViviendaActivity
import com.example.casehive.Activitys.Viviendas.FavoritosActivity
import com.example.casehive.Activitys.Viviendas.MisViviendasActivity
import com.example.casehive.R
import com.example.casehive.adapters.ViviendaAdapter
import com.example.casehive.models.Vivienda
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private var isDarkMode = false

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
        val btnMisViviendas = findViewById<Button>(R.id.btnMisViviendas)
        btnMisViviendas.setOnClickListener {
            val intent = Intent(this, MisViviendasActivity::class.java)
            startActivity(intent)
        }
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

        val btnToggleTheme = findViewById<ImageButton>(R.id.btnToggleTheme)
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        btnToggleTheme.setOnClickListener {
            isDarkMode = !isDarkMode
            val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }




    }

    private fun cargarViviendas() {
        val viviendasRef = database.child("viviendas")
        viviendasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaViviendas.clear()
                for (item in snapshot.children) {
                    try {
                        val viviendaMap = item.value as Map<*, *>

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

                        listaViviendas.add(vivienda)
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error al deserializar vivienda: ${e.message}")
                    }
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
