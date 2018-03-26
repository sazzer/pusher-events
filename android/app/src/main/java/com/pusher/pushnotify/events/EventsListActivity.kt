package com.pusher.pushnotify.events

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ListView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import android.R.menu
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import com.pusher.pushnotifications.PushNotifications


class EventsListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val EVENTS_ENDPOINT = "http://10.0.2.2:8080/events"

    private lateinit var recordAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventslist)

        recordAdapter = EventAdapter(this)
        val recordsView = findViewById<View>(R.id.records_view) as ListView
        recordsView.setAdapter(recordAdapter)
        recordsView.onItemClickListener = this

        refreshEventsList()

        PushNotifications.start(getApplicationContext(), "aa222ff6-54d8-450a-9f53-22411b5e8502");
        PushNotifications.subscribe("CREATED");
        PushNotifications.subscribe("USER_" + (application as EventsApplication).username);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.eventslist, menu)
        return true
    }

    private fun refreshEventsList() {
        val client = AsyncHttpClient()
        client.get(EVENTS_ENDPOINT, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, response: JSONArray) {
                super.onSuccess(statusCode, headers, response)
                runOnUiThread {
                    val events = IntRange(0, response.length() - 1)
                            .map { index -> response.getJSONObject(index) }
                            .map { obj ->
                                Event(
                                        id = obj.getString("id"),
                                        name = obj.getString("name"),
                                        description = obj.getString("description"),
                                        start = obj.getString("start")
                                )
                            }

                    recordAdapter.records = events
                }
            }
        })
    }

    fun onClickNewEvent(v: MenuItem) {
        startActivity(Intent(this, CreateEventsActivity::class.java))
    }

    fun onClickRefresh(v: MenuItem) {
        refreshEventsList()
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val eventViewHolder = view.tag as EventViewHolder
        val intent = Intent(this, ViewEventActivity::class.java)
        intent.putExtra("event", eventViewHolder.id)
        startActivity(intent)
    }

}