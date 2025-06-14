package com.example.casehive.Activitys.Viviendas

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.casehive.R
import com.example.casehive.models.Vivienda
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class CasasCercanasActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference
    private var ubicacionActual: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_casas_cercanas)

        database = FirebaseDatabase.getInstance().getReference("viviendas")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        solicitarUbicacionYMostrarViviendas()
    }

    // Pedir permisos si hace falta y obtener ubicación actual
    private fun solicitarUbicacionYMostrarViviendas() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1234)
            return
        }
        obtenerUbicacionYMostrarViviendas()
    }

    private fun obtenerUbicacionYMostrarViviendas() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                ubicacionActual = location
                mMap.isMyLocationEnabled = true
                cargarViviendasCercanas()
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1234 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionYMostrarViviendas()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarViviendasCercanas() {

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val viviendasCercanas = mutableListOf<Pair<Vivienda, Float>>() // Vivienda y distancia

                for (viviendaSnapshot in snapshot.children) {

                    val vivienda = snapshotToVivienda(viviendaSnapshot)
                    if (vivienda != null && vivienda.latitud != null && vivienda.longitud != null && ubicacionActual != null) {
                        val resultado = FloatArray(1)
                        Location.distanceBetween(
                            ubicacionActual!!.latitude,
                            ubicacionActual!!.longitude,
                            vivienda.latitud!!,
                            vivienda.longitud!!,
                            resultado

                        )
                        viviendasCercanas.add(Pair(vivienda, resultado[0])) // metros

                    }
                }

                val masCercanas = viviendasCercanas.sortedBy { it.second }

                for ((vivienda, distancia) in masCercanas) {
                    val latLng = LatLng(vivienda.latitud!!, vivienda.longitud!!)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(vivienda.tipo)
                            .snippet("${vivienda.ubicación} - ${vivienda.precio} € - ${(distancia / 1000).format(2)} km")
                    )
                }

                ubicacionActual?.let {
                    val miPos = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPos, 13f))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CasasCercanasActivity, "Error al cargar viviendas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun snapshotToVivienda(snapshot: DataSnapshot): Vivienda? {
        val viviendaMap = snapshot.value as? Map<*, *> ?: return null

        return Vivienda(
            id = snapshot.key ?: "",
            tipo = viviendaMap["tipo"] as? String ?: "",
            ubicación = viviendaMap["ubicación"] as? String ?: "",
            descripción = viviendaMap["descripción"] as? String ?: "",
            habitaciones = (viviendaMap["habitaciones"] as? Long)?.toInt() ?: 0,
            baños = (viviendaMap["baños"] as? Long)?.toInt() ?: 0,
            superficie_m2 = (viviendaMap["superficie_m2"] as? Long)?.toInt() ?: 0,
            plantas = (viviendaMap["plantas"] as? Long)?.toInt() ?: 0,
            precio = (viviendaMap["precio"] as? Long)?.toInt() ?: 0,
            año_construcción = (viviendaMap["año_construcción"] as? Long)?.toInt() ?: 0,
            estado = viviendaMap["estado"] as? String ?: "",
            certificación_energética = viviendaMap["certificación_energética"] as? String ?: "",
            extras = convertirALista(viviendaMap["extras"]),
            imágenes = convertirALista(viviendaMap["imágenes"]),
            creadorId = viviendaMap["creadorId"] as? String ?: "",
            latitud = (viviendaMap["latitud"] as? Double)
                ?: (viviendaMap["latitud"] as? Long)?.toDouble(),
            longitud = (viviendaMap["longitud"] as? Double)
                ?: (viviendaMap["longitud"] as? Long)?.toDouble()
        )
    }

    private fun convertirALista(raw: Any?): List<String> {
        return when (raw) {
            is List<*> -> raw.filterIsInstance<String>()
            is Map<*, *> -> raw.values.filterIsInstance<String>()
            else -> emptyList()
        }
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
}
