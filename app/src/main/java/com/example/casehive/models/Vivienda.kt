package com.example.casehive.models

data class Vivienda(
    var id: String = "", // ✅ Añadido para manejar identificadores únicos desde Firebase
    val tipo: String = "",
    val ubicación: String = "",
    val superficie_m2: Int = 0,
    val habitaciones: Int = 0,
    val baños: Int = 0,
    val plantas: Int = 0,
    val precio: Int = 0,
    val año_construcción: Int = 0,
    val estado: String = "",
    val certificación_energética: String = "",
    val descripción: String = "",
    val imágenes: List<String> = emptyList(),
    val extras: List<String> = emptyList(),
    val creadorId: String = ""
)


