package com.example.tasku

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object TaskPreferences {

    private const val PREFS_NAME = "tasku_preferences"
    private const val KEY_TASKS = "saved_tasks"

    // Guardar las tareas
    fun saveTasks(
        context: Context,
        tasks: List<Task>
    ) {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val jsonArray = JSONArray()

        tasks.forEach { task ->

            val jsonObject = JSONObject()

            jsonObject.put("id", task.id)
            jsonObject.put("nombre", task.nombre)
            jsonObject.put("descripcion", task.descripcion)
            jsonObject.put("asignatura", task.asignatura)
            jsonObject.put("fechaEntrega", task.fechaEntrega)
            jsonObject.put("completada", task.completada)

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(KEY_TASKS, jsonArray.toString())
            .apply()
    }

    // Recuperar las tareas
    fun loadTasks(context: Context): List<Task> {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val jsonString = preferences.getString(
            KEY_TASKS,
            "[]"
        ) ?: "[]"

        val jsonArray = JSONArray(jsonString)

        val tasks = mutableListOf<Task>()

        for (i in 0 until jsonArray.length()) {

            val jsonObject = jsonArray.getJSONObject(i)

            val task = Task(
                id = jsonObject.getInt("id"),
                nombre = jsonObject.getString("nombre"),
                descripcion = jsonObject.getString("descripcion"),
                asignatura = jsonObject.getString("asignatura"),
                fechaEntrega = jsonObject.getString("fechaEntrega"),
                completada = jsonObject.getBoolean("completada")
            )

            tasks.add(task)
        }

        return tasks
    }
}