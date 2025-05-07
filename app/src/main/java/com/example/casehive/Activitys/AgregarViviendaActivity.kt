package com.example.casehive.Activitys

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casehive.R
import com.example.casehive.adapters.ImagenesAdapter
import com.example.casehive.models.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AgregarViviendaActivity : AppCompatActivity() {

    private lateinit var imagenesSeleccionadas: MutableList<Uri>
    private lateinit var recyclerImagenes: RecyclerView
    private lateinit var adapterImagenes: ImagenesAdapter

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vivienda)

        val tipo = findViewById<EditText>(R.id.etTipo)
        val ubicacion = findViewById<EditText>(R.id.etUbicacion)
        val descripcion = findViewById<EditText>(R.id.etDescripcion)
        val superficie = findViewById<EditText>(R.id.etSuperficie)
        val habitaciones = findViewById<EditText>(R.id.etHabitaciones)
        val baños = findViewById<EditText>(R.id.etBanos)
        val plantas = findViewById<EditText>(R.id.etPlantas)
        val precio = findViewById<EditText>(R.id.etPrecio)
        val año = findViewById<EditText>(R.id.etAnoConstruccion)
        val estado = findViewById<EditText>(R.id.etEstado)
        val certificacion = findViewById<EditText>(R.id.etCertificacion)
        val extras = findViewById<EditText>(R.id.etExtras)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarVivienda)
        val btnSeleccionarImagenes = findViewById<Button>(R.id.btnSeleccionarImagenes)

        imagenesSeleccionadas = mutableListOf()
        recyclerImagenes = findViewById(R.id.recyclerImagenes)
        adapterImagenes = ImagenesAdapter(imagenesSeleccionadas)
        recyclerImagenes.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerImagenes.adapter = adapterImagenes

        val launcher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                imagenesSeleccionadas.clear()
                imagenesSeleccionadas.addAll(uris)
                adapterImagenes.notifyDataSetChanged()
            }
        }

        btnSeleccionarImagenes.setOnClickListener {
            launcher.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            Toast.makeText(this, "Intentando guardar...", Toast.LENGTH_SHORT).show()

            val vivienda = Vivienda(
                tipo = tipo.text.toString(),
                ubicación = ubicacion.text.toString(),
                descripción = descripcion.text.toString(),
                superficie_m2 = superficie.text.toString().toIntOrNull() ?: 0,
                habitaciones = habitaciones.text.toString().toIntOrNull() ?: 0,
                baños = baños.text.toString().toIntOrNull() ?: 0,
                plantas = plantas.text.toString().toIntOrNull() ?: 0,
                precio = precio.text.toString().toIntOrNull() ?: 0,
                año_construcción = año.text.toString().toIntOrNull() ?: 0,
                estado = estado.text.toString(),
                certificación_energética = certificacion.text.toString(),
                extras = extras.text.toString().split(",").map { it.trim() },
                imágenes = imagenesSeleccionadas.map { it.toString() },
                creadorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )
            Log.d("AgregarVivienda", "Enviando vivienda: $vivienda")

            val ref = FirebaseDatabase.getInstance().getReference("viviendas").push()
            ref.setValue(vivienda).addOnSuccessListener {
                Log.d("AgregarVivienda", "Guardado OK")
                Toast.makeText(this, "Vivienda guardada", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                Log.e("AgregarVivienda", "Error al guardar: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }

        }
    }
}
