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
                listOf("global-event-$action"),
                mapOf(
                        "fcm" to mapOf(
                                "notification" to mapOf(
                                        "title" to "New event: ${event.name}",
                                        "body" to "${event.description}. Start time: ${event.start}"
                                )
                        )
                )
        )
    }

    fun emitForEvent(action: String, event: Event) {
        pusher.publish(
                listOf(event.id!!),
                mapOf(
                        "fcm" to mapOf(
                                "notification" to mapOf(
                                        "title" to event.name,
                                        "body" to "${event.description}. Start time: ${event.start}"
                                )
                        )
                )
        )
    }

    fun emitForSubscription(action: String, user: String, event: Event) {
        val message = if (action == "SUBSCRIBED") {
            "$user is interested"
        } else {
            "$user is not interested"
        }

        pusher.publish(
                listOf(event.id!!),
                mapOf(
                        "fcm" to mapOf(
                                "notification" to mapOf(
                                        "title" to event.name,
                                        "body" to message
                                )
                        )
                )
        )
    }
}
