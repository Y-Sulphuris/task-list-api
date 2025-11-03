package com.example

import com.example.model.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.concurrent.CopyOnWriteArrayList

// по идее можно просто ArrayList использовать
// но лучше не надо
val tasks = CopyOnWriteArrayList<Task>()
var nextId = 1

fun Application.configureRouting() {
    routing {
        post("/tasks") {
            val received = call.receive<TaskCreateVM>()
            if (received.title.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Title. I don't see it."))
                return@post
            }
            val newTask = Task(
                id = nextId++,
                title = received.title,
                description = received.description,
                completed = received.completed
            )
            tasks.add(newTask)
            call.respond(HttpStatusCode.Created, newTask)
        }
        get("/tasks") {
            val completedParam = call.request.queryParameters["completed"]?.toBooleanStrictOrNull() // вот это кстати легендарная функция, точно стоила включения в стандартную библиотекку XD
            val filtered = if (completedParam != null) tasks.filter {
                it.completed == completedParam
            }
            else tasks

            call.respond(filtered)
        }
        get("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val task = tasks.find {
                it.id == id
            }

            if (task != null) {
                call.respond(task)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
            }
        }
        put("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val existing = tasks.find { it.id == id }
            if (existing == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
                return@put // муахаха
            }

            val update = call.receive<TaskUpdateVM>()
            val updated = existing.copy(
                title = update.title ?: existing.title,
                description = update.description ?: existing.description,
                completed = update.completed ?: existing.completed
            )

            tasks[tasks.indexOf(existing)] = updated
            call.respond(updated)
        }
        delete("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val removed = tasks.removeIf { it.id == id }
            if (removed) {
                call.respond(HttpStatusCode.NoContent)
            }
            else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
        }
    }
}
