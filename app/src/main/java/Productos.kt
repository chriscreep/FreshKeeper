package com.example.freshkeeper_app

data class Producto(
    val categoria: String,
    val nombre: String = "",
    val fechaVencimiento: String,
    val fechaCompra: String,
    val docId: String = ""
)
