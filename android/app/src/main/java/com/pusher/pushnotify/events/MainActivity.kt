package com.pusher.pushnotify.events

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.pusher.pushnotifications.PushNotifications
import cz.msebera.android.httpclient.Header
import org.json.JSONArray


private val EVENTS_ENDPOINT = "http://10.0.2.2:8080/events"

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var recordAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PushNotifications.start(getApplicationContext(), "pusher.instanceId");

        recordAdapter = EventAdapter(this)
        val recordsView = findViewById<View>(R.id.records_view) as ListView
        recordsView.setAdapter(recordAdapter)

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

        recordsView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val eventViewHolder = view.tag as EventViewHolder

        Log.v("MainActivity", "Cicked id: " + eventViewHolder.id)
        PushNotifications.subscribe(eventViewHolder.id);
    }
}
