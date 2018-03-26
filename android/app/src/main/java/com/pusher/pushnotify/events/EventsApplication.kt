package com.pusher.pushnotify.events

import android.app.Application
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.PushNotificationReceivedListener
import com.pusher.pushnotifications.fcm.FCMMessagingService
import android.app.NotificationManager
import android.content.Context
import android.app.NotificationChannel
import android.os.Build
import android.app.PendingIntent
import android.content.Intent






class EventsApplication : Application() {
    var username: String? = null

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("events",
                    "Pusher Events",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }


        FCMMessagingService.setOnMessageReceivedListener(object : PushNotificationReceivedListener {
            override fun onMessageReceived(remoteMessage: RemoteMessage) {
                val action = remoteMessage.data["action"]

                if (action == "CREATED") {
                    showCreatedNotification(remoteMessage.data)
                } else if (action == "SUBSCRIBED") {
                    showSubscribedNotification(remoteMessage.data)
                } else if (action == "UNSUBSCRIBED") {
                    showUnsubscribedNotification(remoteMessage.data)
                } else if (action == "RECOMMENDED") {
                    showRecommendedNotification(remoteMessage.data)
                }
            }
        })
    }

    private fun showRecommendedNotification(data: Map<String, String>) {
        Log.v("EventsApplication", "Received Recommended Notification: " + data.toString())

        val viewIntent = Intent(this, ViewEventActivity::class.java)
        viewIntent.putExtra("event", data["id"])
        val pendingViewIntent = PendingIntent.getActivity(applicationContext, 0, viewIntent, 0)

        val interestedIntent = Intent(this, ViewEventActivity::class.java)
        interestedIntent.putExtra("event", data["id"])
        interestedIntent.putExtra("trigger", "interested")
        val pendingInterestedIntent = PendingIntent.getActivity(applicationContext, 1, interestedIntent, 0)

        val notification = NotificationCompat.Builder(this, "events")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event " + data["name"] + " has been recommended to you")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "View", pendingViewIntent).build())
                .addAction(NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Interested", pendingInterestedIntent).build())

        notificationManager.notify(0, notification.build())
    }

    private fun showUnsubscribedNotification(data: Map<String, String>) {
        Log.v("EventsApplication", "Received Unsubscribed Notification: " + data.toString())

        val intent = Intent(this, ViewEventActivity::class.java)
        intent.putExtra("event", data["id"])
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, "events")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data["user"] + " is no longer interested in " + data["name"])
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        notificationManager.notify(0, notification.build())

    }

    private fun showSubscribedNotification(data: Map<String, String>) {
        Log.v("EventsApplication", "Received Subscribed Notification: " + data.toString())

        val intent = Intent(this, ViewEventActivity::class.java)
        intent.putExtra("event", data["id"])
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, "events")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data["user"] + " is interested in " + data["name"])
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        notificationManager.notify(0, notification.build())
    }

    private fun showCreatedNotification(data: Map<String, String>) {
        Log.v("EventsApplication", "Received Created Notification: " + data.toString())

        val intent = Intent(applicationContext, EventsListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, "events")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New event: " + data["name"])
                .setContentText(data["description"])
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        notificationManager.notify(0, notification.build())
    }
}