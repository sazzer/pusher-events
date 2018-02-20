package com.pusher.eventsbackend

import java.time.Instant

data class Event(
        val id: String?,
        val name: String,
        val description: String,
        val start: Instant
)
