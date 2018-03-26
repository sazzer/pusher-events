package com.pusher.eventsbackend

import com.pusher.pushnotifications.PushNotifications
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EventNotifier(
        @Value("\${pusher.instanceId}") private val instanceId: String,
        @Value("\${pusher.secretKey}") private val secretKey: String
) {
    private val pusher = PushNotifications(instanceId, secretKey)

    fun emitGlobal(action: String, event: Event) {
        pusher.publish(
                listOf(action),
                mapOf(
                        "fcm" to mapOf(
                                "data" to mapOf(
                                        "action" to action,
                                        "id" to event.id,
                                        "name" to event.name,
                                        "description" to event.description,
                                        "start" to event.start
                                )
                        )
                )
        )
    }

    fun emitForEvent(action: String, event: Event) {
        pusher.publish(
                listOf("EVENT_" + event.id!!),
                mapOf(
                        "fcm" to mapOf(
                                "data" to mapOf(
                                        "action" to action,
                                        "id" to event.id,
                                        "name" to event.name,
                                        "description" to event.description,
                                        "start" to event.start
                                )
                        )
                )
        )
    }

    fun emitForUsers(action: String, users: List<String>, event: Event) {
        pusher.publish(
                users.map { "USER_$it" },
                mapOf(
                        "fcm" to mapOf(
                                "data" to mapOf(
                                        "action" to action,
                                        "id" to event.id,
                                        "name" to event.name,
                                        "description" to event.description,
                                        "start" to event.start
                                )
                        )
                )
        )
    }

    fun emitFromUser(action: String, user: String, event: Event) {
        pusher.publish(
                listOf("EVENT_" + event.id!!),
                mapOf(
                        "fcm" to mapOf(
                                "data" to mapOf(
                                        "user" to user,
                                        "action" to action,
                                        "id" to event.id,
                                        "name" to event.name,
                                        "description" to event.description,
                                        "start" to event.start
                                )
                        )
                )
        )
    }
}
