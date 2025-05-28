package com.example.casehive.Activitys.Viviendas.Detalles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.casehive.R
import com.google.android.material.button.MaterialButton

class DetalleViviendaActivity : AppCompatActivity() {

    private var imagenIndex = 0
    private lateinit var imagenes: List<String>
    private lateinit var imageView: ImageView
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_vivienda)

        val tipo = intent.getStringExtra("tipo") ?: ""
        val ubicacion = intent.getStringExtra("ubicacion") ?: ""
        val superficie = intent.getIntExtra("superficie", 0)
        val habitaciones = intent.getIntExtra("habitaciones", 0)
        val baños = intent.getIntExtra("baños", 0)
        val plantas = intent.getIntExtra("plantas", 0)
        val precio = intent.getIntExtra("precio", 0)
        val añoConstruccion = intent.getIntExtra("año_construccion", 0)
        val estado = intent.getStringExtra("estado") ?: ""
        val certificacion = intent.getStringExtra("certificacion") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val extras = intent.getStringArrayListExtra("extras") ?: arrayListOf()
        imagenes = intent.getStringArrayListExtra("imagenes") ?: arrayListOf()
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)

        imageView = findViewById(R.id.imageVivienda)
        btnNext = findViewById(R.id.btnNextImage)
        btnPrev = findViewById(R.id.btnPrevImage)

        if (imagenes.isNotEmpty()) {
            Glide.with(this).load(imagenes[imagenIndex]).into(imageView)
        }

        btnNext.setOnClickListener {
            imagenIndex = (imagenIndex + 1) % imagenes.size
            Glide.with(this).load(imagenes[imagenIndex]).into(imageView)
        }

        btnPrev.setOnClickListener {
            imagenIndex = if (imagenIndex - 1 < 0) imagenes.size - 1 else imagenIndex - 1
            Glide.with(this).load(imagenes[imagenIndex]).into(imageView)
        }


        findViewById<TextView>(R.id.textTipo).text = tipo
        findViewById<TextView>(R.id.textUbicacion).text = ubicacion
        findViewById<TextView>(R.id.textDescripcion).text = descripcion
        findViewById<TextView>(R.id.textHabitaciones).text = "Habitaciones: $habitaciones"
        findViewById<TextView>(R.id.textBanos).text = "Baños: $baños"
        findViewById<TextView>(R.id.textSuperficie).text = "$superficie m²"
        findViewById<TextView>(R.id.textPlantas).text = "Plantas: $plantas"
        findViewById<TextView>(R.id.textPrecio).text = "€ $precio"
        findViewById<TextView>(R.id.textEstado).text = "Estado: $estado"
        findViewById<TextView>(R.id.textAnio).text = "Construcción: $añoConstruccion"
        findViewById<TextView>(R.id.textCertificacion).text = "Certificación: $certificacion"
        findViewById<TextView>(R.id.textExtras).text = "Extras: ${extras.joinToString(", ")}"

        val ubicacionUrl = if (latitud != 0.0 && longitud != 0.0) {
            "https://www.google.com/maps/search/?api=1&query=$latitud,$longitud"
        } else {
            "https://www.google.com/maps/search/?api=1&query=${ubicacion.replace(" ", "+")}"
        }

        findViewById<MaterialButton>(R.id.btnVerEnMapa).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ubicacionUrl))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
    }
}
