package com.example.tasku

import androidx.compose.runtime.saveable.rememberSaveable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                var mostrarFormulario by remember {
                    mutableStateOf(false)
                }

                var tareas by remember {
                    mutableStateOf(
                        TaskPreferences.loadTasks(this@MainActivity)
                    )
                }

                if (mostrarFormulario) {

                    AddTaskScreen(
                        onSave = { tarea ->

                            val nuevoId = (tareas.maxOfOrNull {
                                it.id
                            } ?: 0) + 1

                            val nuevaLista = tareas + tarea.copy(
                                id = nuevoId
                            )

                            tareas = nuevaLista

                            TaskPreferences.saveTasks(
                                this@MainActivity,
                                nuevaLista
                            )

                            mostrarFormulario = false
                        },
                        onCancel = {
                            mostrarFormulario = false
                        }
                    )

                } else {

                    TaskUHomeScreen(
                        tareas = tareas,

                        onAddTask = {
                            mostrarFormulario = true
                        },

                        onToggleTask = { id ->

                            val nuevaLista = tareas.map { tarea ->

                                if (tarea.id == id) {
                                    tarea.copy(
                                        completada = !tarea.completada
                                    )
                                } else {
                                    tarea
                                }
                            }

                            tareas = nuevaLista

                            TaskPreferences.saveTasks(
                                this@MainActivity,
                                nuevaLista
                            )
                        },

                        onDeleteTask = { id ->

                            val nuevaLista = tareas.filter {
                                it.id != id
                            }

                            tareas = nuevaLista

                            TaskPreferences.saveTasks(
                                this@MainActivity,
                                nuevaLista
                            )
                        }
                    )
                }
            }
        }
    }
}

// COLORES DE TASKU

val Azul = Color(0xFF243B64)
val Verde = Color(0xFF43A889)
val Fondo = Color(0xFFFFFFFF)

// PANTALLA PRINCIPAL

@Composable
fun TaskUHomeScreen(
    tareas: List<Task>,
    onAddTask: () -> Unit,
    onToggleTask: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit
) {

    val pendientes = tareas.count {
        !it.completada
    }

    val completadas = tareas.count {
        it.completada
    }

    Scaffold(
        containerColor = Fondo
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            Text(
                text = "TaskU",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Azul
            )

            Text(
                text = "Organiza tu vida académica",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "¡Hola, estudiante!",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Este es el resumen de tus actividades.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                TaskCard(
                    titulo = "Pendientes",
                    cantidad = pendientes.toString(),
                    color = Azul,
                    modifier = Modifier.weight(1f)
                )

                TaskCard(
                    titulo = "Completadas",
                    cantidad = completadas.toString(),
                    color = Verde,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Mis actividades",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (tareas.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Aún no tienes tareas",
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Agrega tu primera actividad académica.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(
                        items = tareas,
                        key = { it.id }
                    ) { tarea ->

                        TaskItem(
                            tarea = tarea,
                            onToggle = {
                                onToggleTask(tarea.id)
                            },
                            onDelete = {
                                onDeleteTask(tarea.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = onAddTask,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Azul
                )
            ) {

                Text(
                    text = "+ Agregar nueva tarea",
                    fontSize = 16.sp
                )
            }
        }
    }
}

// TARJETAS DE ESTADÍSTICAS

@Composable
fun TaskCard(
    titulo: String,
    cantidad: String,
    color: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = cantidad,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

// ELEMENTO INDIVIDUAL DE LA LISTA

@Composable
fun TaskItem(
    tarea: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = {
                        onToggle()
                    }
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = tarea.nombre,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = tarea.asignatura,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                TextButton(
                    onClick = onDelete
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color.Red
                    )
                }
            }

            Text(
                text = tarea.descripcion,
                fontSize = 13.sp
            )

            Text(
                text = "Entrega: ${tarea.fechaEntrega}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (tarea.completada) {
                Text(
                    text = "Tarea completada",
                    color = Verde,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}