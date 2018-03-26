package com.pusher.pushnotify.events

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONArray

class ShareEventDialog : DialogFragment() {
    private val EVENTS_ENDPOINT = "http://10.0.2.2:8080/events/"

    lateinit var event: String
    lateinit var friends: List<Friend>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val names = friends.map { it.name }
        val selected = mutableSetOf<String>()
        return AlertDialog.Builder(activity)
                .setTitle("Share")
                .setMultiChoiceItems(names.toTypedArray(), null) { dialog, which, isChecked ->
                    val friend = friends[which]
                    if (isChecked) {
                        selected.add(friend.id)
                    } else {
                        selected.remove(friend.id)
                    }
                }
                .setPositiveButton("Share") { dialog, which ->
                    Log.v("ShareEventDialog", "Sharing with: " + selected)
                    val client = AsyncHttpClient()
                    val request = JSONArray(selected)

                    client.post(null,EVENTS_ENDPOINT + event + "/share", StringEntity(request.toString()), "application/json",
                            object : JsonHttpResponseHandler() {

                            })
                }
                .setNegativeButton("Cancel") { dialog, which -> }
                .create()
    }
}