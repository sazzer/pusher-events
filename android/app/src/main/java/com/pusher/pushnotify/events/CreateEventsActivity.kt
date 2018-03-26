package com.pusher.pushnotify.events

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONObject

class CreateEventsActivity : AppCompatActivity() {
    private val EVENTS_ENDPOINT = "http://10.0.2.2:8080/events"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createevent)
    }

    fun onClickCreate(v: View) {
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val startInput = findViewById<EditText>(R.id.startInput)

        val name = nameInput.text.toString()
        val description = descriptionInput.text.toString()
        val start = startInput.text.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "No event name entered!", Toast.LENGTH_LONG).show()
        } else if (start.isBlank()) {
            Toast.makeText(this, "No start time entered!", Toast.LENGTH_LONG).show()
        } else {
            val transitionIntent = Intent(this, EventsListActivity::class.java)

            val client = AsyncHttpClient()
            val request = JSONObject(mapOf(
                    "name" to name,
                    "description" to description,
                    "start" to start
            ))
            client.post(applicationContext, EVENTS_ENDPOINT, StringEntity(request.toString()), "application/json", object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>, response: JSONObject) {
                    startActivity(transitionIntent)
                }
            })
        }
    }
}