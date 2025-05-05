package com.example.casehive.Activitys

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.casehive.R
import com.example.casehive.models.Vivienda
import com.google.firebase.database.FirebaseDatabase

class EditarViviendaActivity : AppCompatActivity() {

    private lateinit var viviendaId: String
    private val database = FirebaseDatabase.getInstance().getReference("viviendas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_vivienda)

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
        val btnGuardar = findViewById<Button>(R.id.btnGuardarVivienda)

        val vivienda = intent.getSerializableExtra("vivienda") as? Vivienda
        viviendaId = intent.getStringExtra("viviendaId") ?: return

        vivienda?.let {
            tipo.setText(it.tipo)
            ubicacion.setText(it.ubicación)
            descripcion.setText(it.descripción)
            habitaciones.setText(it.habitaciones.toString())
            baños.setText(it.baños.toString())
            superficie.setText(it.superficie_m2.toString())
            plantas.setText(it.plantas.toString())
            precio.setText(it.precio.toString())
            año.setText(it.año_construcción.toString())
            estado.setText(it.estado)
            certificacion.setText(it.certificación_energética)
            extras.setText(it.extras.joinToString(", "))
        }

        btnGuardar.setOnClickListener {
            val viviendaActualizada = vivienda?.copy(
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
                extras = extras.text.toString().split(",").map { it.trim() }
            )

            viviendaActualizada?.let { actualizada ->
                database.child(viviendaId).setValue(actualizada).addOnSuccessListener {
                    Toast.makeText(this, "Vivienda actualizada", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
