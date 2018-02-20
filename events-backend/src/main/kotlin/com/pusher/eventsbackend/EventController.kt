package com.pusher.eventsbackend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/events")
class EventController(@Autowired private val eventNotifier: EventNotifier) {
    private val events = mutableListOf(
            Event(
                    id = "xmas",
                    name = "Christmas",
                    description = "It's the most wonderful time of the year",
                    start = Instant.parse("2018-12-25T00:00:00Z")
            )
    )

    private val interest: MutableMap<String, MutableSet<String>> = mutableMapOf()

    @RequestMapping
    fun getEvents() = events

    @RequestMapping("/{id}")
    fun getEvent(@PathVariable("id") id: String) =
            events.find { it.id == id }
                    ?.let { ResponseEntity.ok(it) }
                    ?: ResponseEntity.notFound().build()

    @RequestMapping(method = [RequestMethod.POST])
    fun createEvent(@RequestBody event: Event): Event {
        val newEvent = Event(
                id = UUID.randomUUID().toString(),
                name = event.name,
                description = event.description,
                start = event.start
        )
        events.add(newEvent)
        return newEvent
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
    fun deleteEvent(@PathVariable("id") id: String) {
        events.find { it.id == id }
                ?.let { eventNotifier.emitForEvent("DELETED", it) }
        events.removeIf { it.id == id }
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT])
    fun updateEvent(@PathVariable("id") id: String, @RequestBody event: Event): ResponseEntity<Event>? {
        return if (events.removeIf { it.id == id }) {
            val newEvent = Event(
                    id = id,
                    name = event.name,
                    description = event.description,
                    start = event.start
            )
            events.add(newEvent)
            eventNotifier.emitForEvent("UPDATED", newEvent)
            ResponseEntity.ok(newEvent)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @RequestMapping("/{id}/interest")
    fun getInterest(@PathVariable("id") event: String) =
            interest.getOrElse(event) {
                mutableSetOf()
            }

    @RequestMapping(value = ["/{id}/interest/{user}"], method = [RequestMethod.PUT])
    fun registerInterest(@PathVariable("id") event: String, @PathVariable("user") user: String) {
        val eventInterest = interest.getOrPut(event) {
            mutableSetOf()
        }

        eventInterest.add(user)
        events.find { it.id == event }
                ?.let { eventNotifier.emitForSubscription("SUBSCRIBED", user, it) }
    }

    @RequestMapping(value = ["/{id}/interest/{user}"], method = [RequestMethod.DELETE])
    fun unregisterInterest(@PathVariable("id") event: String, @PathVariable("user") user: String) {
        val eventInterest = interest.getOrPut(event) {
            mutableSetOf()
        }

        eventInterest.remove(user)
        events.find { it.id == event }
                ?.let { eventNotifier.emitForSubscription("UNSUBSCRIBED", user, it) }
    }
}
