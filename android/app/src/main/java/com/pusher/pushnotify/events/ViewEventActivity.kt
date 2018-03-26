package com.pusher.pushnotify.events

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.pusher.pushnotifications.PushNotifications
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class ViewEventActivity : AppCompatActivity() {
    private val EVENTS_ENDPOINT = "http://10.0.2.2:8080/events/"
    private val USERS_ENDPOINT = "http://10.0.2.2:8080/users/"

    private lateinit var eventId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewevent)
        eventId = intent.getStringExtra("event")

        refreshEventDetails()

        val trigger = intent.getStringExtra("trigger")
        if (trigger == "interested") {
            onClickInterested(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view, menu)
        return true
    }

    fun onClickShare(v: MenuItem) {
        val client = AsyncHttpClient()
        client.get(USERS_ENDPOINT + (application as EventsApplication).username + "/friends", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray) {
                super.onSuccess(statusCode, headers, response)

                val friends = IntRange(0, response.length() - 1)
                        .map { index -> response.getJSONObject(index) }
                        .map { obj ->
                            Friend(
                                    id = obj.getString("id"),
                                    name = obj.getString("name")
                            )
                        }

                runOnUiThread {
                    val dialog = ShareEventDialog()
                    dialog.event = eventId
                    dialog.friends = friends
                    dialog.show(supportFragmentManager, "ShareEventDialog")
                }
            }
        })
    }

    private fun refreshEventDetails() {
        val client = AsyncHttpClient()
        client.get(EVENTS_ENDPOINT + eventId, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, response: JSONObject) {
                super.onSuccess(statusCode, headers, response)

                val nameDisplay = findViewById<TextView>(R.id.nameValue)
                val descriptionDisplay = findViewById<TextView>(R.id.descriptionValue)
                val startDisplay = findViewById<TextView>(R.id.startValue)

                val name = response.getString("name")
                val description = response.getString("description")
                val start = response.getString("start")

                runOnUiThread {
                    nameDisplay.text = name
                    descriptionDisplay.text = description
                    startDisplay.text = start
                }
            }
        })

        client.get(EVENTS_ENDPOINT + eventId + "/interest", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, response: JSONArray) {
                super.onSuccess(statusCode, headers, response)

                val numberInterestedDisplay = findViewById<TextView>(R.id.numberInterestValue)
                val interestedButton = findViewById<Button>(R.id.interestedButton)
                val notInterestedButton = findViewById<Button>(R.id.disinterestedButton)

                val numberInterested = response.length().toString()
                val imInterested = IntRange(0, response.length() - 1)
                        .map { index -> response.getString(index) }
                        .contains((application as EventsApplication).username)

                runOnUiThread {
                    numberInterestedDisplay.text = numberInterested

                    if (imInterested) {
                        interestedButton.visibility = View.GONE
                        notInterestedButton.visibility = View.VISIBLE
                    } else {
                        interestedButton.visibility = View.VISIBLE
                        notInterestedButton.visibility = View.GONE
                    }
                }
            }
        })
    }

    fun onClickInterested(v: View?) {
        val client = AsyncHttpClient()
        val username = (application as EventsApplication).username

        client.put(EVENTS_ENDPOINT + eventId + "/interest/" + username, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                PushNotifications.subscribe("EVENT_" + eventId);
                runOnUiThread {
                    refreshEventDetails()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                runOnUiThread {
                    refreshEventDetails()
                }
            }
        })
    }

    fun onClickDisinterested(v: View) {
        val client = AsyncHttpClient()
        val username = (application as EventsApplication).username

        client.delete(EVENTS_ENDPOINT + eventId + "/interest/" + username, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                PushNotifications.unsubscribe("EVENT_" + eventId);
                runOnUiThread {
                    refreshEventDetails()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                runOnUiThread {
                    refreshEventDetails()
                }
            }
        })
    }
}