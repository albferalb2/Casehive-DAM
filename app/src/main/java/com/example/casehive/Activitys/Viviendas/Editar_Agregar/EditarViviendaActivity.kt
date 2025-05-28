package com.example.casehive.Activitys.Viviendas.Editar_Agregar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.casehive.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class EditarViviendaActivity : AppCompatActivity() {

    private lateinit var viviendaId: String
    private lateinit var dbRef: DatabaseReference

    private lateinit var etTipo: TextInputEditText
    private lateinit var etUbicacion: TextInputEditText
    private lateinit var etSuperficie: TextInputEditText
    private lateinit var etHabitaciones: TextInputEditText
    private lateinit var etBanos: TextInputEditText
    private lateinit var etPlantas: TextInputEditText
    private lateinit var etPrecio: TextInputEditText
    private lateinit var etAnoConstruccion: TextInputEditText
    private lateinit var etEstado: TextInputEditText
    private lateinit var etCertificacion: TextInputEditText
    private lateinit var etExtras: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_vivienda)

        viviendaId = intent.getStringExtra("viviendaId") ?: return finish()
        dbRef = FirebaseDatabase.getInstance().getReference("viviendas").child(viviendaId)

        etTipo = findViewById(R.id.etTipo)
        etUbicacion = findViewById(R.id.etUbicacion)
        etSuperficie = findViewById(R.id.etSuperficie)
        etHabitaciones = findViewById(R.id.etHabitaciones)
        etBanos = findViewById(R.id.etBanos)
        etPlantas = findViewById(R.id.etPlantas)
        etPrecio = findViewById(R.id.etPrecio)
        etAnoConstruccion = findViewById(R.id.etAnoConstruccion)
        etEstado = findViewById(R.id.etEstado)
        etCertificacion = findViewById(R.id.etCertificacion)
        etExtras = findViewById(R.id.etExtras)
        etDescripcion = findViewById(R.id.etDescripcion)

        cargarDatos()

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGuardarVivienda).setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarDatos() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                etTipo.setText(snapshot.child("tipo").getValue(String::class.java))
                etUbicacion.setText(snapshot.child("ubicación").getValue(String::class.java))
                etSuperficie.setText(snapshot.child("superficie_m2").getValue(Long::class.java)?.toString() ?: "")
                etHabitaciones.setText(snapshot.child("habitaciones").getValue(Long::class.java)?.toString() ?: "")
                etBanos.setText(snapshot.child("baños").getValue(Long::class.java)?.toString() ?: "")
                etPlantas.setText(snapshot.child("plantas").getValue(Long::class.java)?.toString() ?: "")
                etPrecio.setText(snapshot.child("precio").getValue(Long::class.java)?.toString() ?: "")
                etAnoConstruccion.setText(snapshot.child("año_construcción").getValue(Long::class.java)?.toString() ?: "")
                etEstado.setText(snapshot.child("esdo").getValue(String::class.java))
                etCertificacion.setText(snapshot.child("certificación_energética").getValue(String::class.java))
                etDescripcion.setText(snapshot.child("descripción").getValue(String::class.java))

                val extrasList = snapshot.child("extras").children.mapNotNull { it.getValue(String::class.java) }
                etExtras.setText(extrasList.joinToString(", "))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarViviendaActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarCambios() {
        val cambios = mutableMapOf<String, Any>()

        cambios["tipo"] = etTipo.text.toString().trim()
        cambios["ubicación"] = etUbicacion.text.toString().trim()
        cambios["superficie_m2"] = etSuperficie.text.toString().toIntOrNull() ?: 0
        cambios["habitaciones"] = etHabitaciones.text.toString().toIntOrNull() ?: 0
        cambios["baños"] = etBanos.text.toString().toIntOrNull() ?: 0
        cambios["plantas"] = etPlantas.text.toString().toIntOrNull() ?: 0
        cambios["precio"] = etPrecio.text.toString().toIntOrNull() ?: 0
        cambios["año_construcción"] = etAnoConstruccion.text.toString().toIntOrNull() ?: 0
        cambios["estado"] = etEstado.text.toString().trim()
        cambios["certificación_energética"] = etCertificacion.text.toString().trim()
        cambios["descripción"] = etDescripcion.text.toString().trim()

        val extrasTexto = etExtras.text.toString().trim()
        cambios["extras"] = if (extrasTexto.isNotEmpty()) extrasTexto.split(",").map { it.trim() } else emptyList<String>()

        dbRef.updateChildren(cambios).addOnSuccessListener {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }
}
