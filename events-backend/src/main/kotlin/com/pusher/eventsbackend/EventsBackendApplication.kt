package com.pusher.eventsbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventsBackendApplication

fun main(args: Array<String>) {
    runApplication<EventsBackendApplication>(*args)
}
