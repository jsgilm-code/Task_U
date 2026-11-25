
package com.example.tasku

data class Task(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val asignatura: String,
    val fechaEntrega: String,
    val completada: Boolean = false
)