package com.example.casehive.Activitys

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.casehive.R
import com.example.casehive.models.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AgregarViviendaActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vivienda)

        val tipo = findViewById<EditText>(R.id.editTipo)
        val ubicacion = findViewById<EditText>(R.id.editUbicacion)
        val descripcion = findViewById<EditText>(R.id.editDescripcion)
        val habitaciones = findViewById<EditText>(R.id.editHabitaciones)
        val baños = findViewById<EditText>(R.id.editBanos)
        val superficie = findViewById<EditText>(R.id.editSuperficie)
        val plantas = findViewById<EditText>(R.id.editPlantas)
        val precio = findViewById<EditText>(R.id.editPrecio)
        val año = findViewById<EditText>(R.id.editAno)
        val estado = findViewById<EditText>(R.id.editEstado)
        val certificacion = findViewById<EditText>(R.id.editCertificacion)
        val extras = findViewById<EditText>(R.id.editExtras)
        val imagenes = findViewById<EditText>(R.id.editImagenes)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarVivienda)

        btnGuardar.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            val vivienda = Vivienda(
                tipo = tipo.text.toString(),
                ubicación = ubicacion.text.toString(),
                descripción = descripcion.text.toString(),
                habitaciones = habitaciones.text.toString().toIntOrNull() ?: 0,
                baños = baños.text.toString().toIntOrNull() ?: 0,
                superficie_m2 = superficie.text.toString().toIntOrNull() ?: 0,
                plantas = plantas.text.toString().toIntOrNull() ?: 1,
                precio = precio.text.toString().toIntOrNull() ?: 0,
                año_construcción = año.text.toString().toIntOrNull() ?: 2020,
                estado = estado.text.toString(),
                certificación_energética = certificacion.text.toString(),
                extras = extras.text.toString().split(",").map { it.trim() },
                imágenes = imagenes.text.toString().split(",").map { it.trim() },
                creadorId = userId
            )

            val ref = FirebaseDatabase.getInstance().getReference("viviendas")
            val id = ref.push().key ?: return@setOnClickListener
            ref.child(id).setValue(vivienda).addOnSuccessListener {
                Toast.makeText(this, "Vivienda añadida correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}