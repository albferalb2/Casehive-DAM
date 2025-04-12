package com.example.casehive.models

data class Chat(
    val mensaje: String = "",
    val remitenteId: String = "",
    val receptorId: String = "",
    val timestamp: Long = 0
)
