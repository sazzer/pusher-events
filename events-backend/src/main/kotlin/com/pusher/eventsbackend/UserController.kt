package com.pusher.eventsbackend

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController {
    private val users = mutableListOf(
            User(id = "john", name = "John"),
            User(id = "paul", name = "Paul"),
            User(id = "george", name = "George"),
            User(id = "ringo", name = "Ringo")
    )

    private val friends = mapOf(
            "john" to listOf("paul", "george", "ringo"),
            "paul" to listOf("john", "george", "ringo"),
            "george" to listOf("john", "paul", "ringo"),
            "ringo" to listOf("john", "paul", "george")
    )

    @RequestMapping("/{id}")
    fun getUser(@PathVariable("id") id: String) =
            users.find { it.id == id }
                    ?.let { ResponseEntity.ok(it) }
                    ?: ResponseEntity.notFound().build()

    @RequestMapping("/{id}/friends")
    fun getFriends(@PathVariable("id") id: String) =
            friends[id]?.map {friendId -> users.find { user -> user.id == friendId } }
                    ?.filterNotNull()
                    ?.let { ResponseEntity.ok(it) }
                    ?: ResponseEntity.notFound().build()
}
