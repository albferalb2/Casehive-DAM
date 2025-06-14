package com.example.casehive.models

data class Vivienda(
    var id: String = "",
    var tipo: String = "",
    var ubicación: String = "",
    var descripción: String = "",
    var habitaciones: Int = 0,
    var baños: Int = 0,
    var superficie_m2: Int = 0,
    var plantas: Int = 0,
    var precio: Int = 0,
    var año_construcción: Int = 0,
    var estado: String = "",
    var certificación_energética: String = "",
    var extras: List<String> = emptyList(),
    var imágenes: List<String> = emptyList(),
    var creadorId: String = "",
    var latitud: Double? = null,
    var longitud: Double? = null
) {
    // Constructor vacío necesario para Firebase
    constructor() : this(
        "", "", "", "", 0, 0, 0, 0, 0, 0,
        "", "", emptyList(), emptyList(), "", null, null
    )
}
