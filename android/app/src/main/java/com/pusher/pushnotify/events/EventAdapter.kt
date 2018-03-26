package com.pusher.pushnotify.events

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class EventAdapter(private val recordContext: Context) : BaseAdapter() {
    var records: List<Event> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val theView = if (view == null) {
            val recordInflator = recordContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val theView = recordInflator.inflate(R.layout.event, null)
            val newEventViewHolder = EventViewHolder(
                    theView.findViewById(R.id.event_name),
                    theView.findViewById(R.id.event_date)
            )
            theView.tag = newEventViewHolder

            theView
        } else {
            view
        }

        val eventViewHolder = theView.tag as EventViewHolder

        val event = getItem(i)
        eventViewHolder.nameView.text = event.name
        eventViewHolder.dateView.text = event.start
        eventViewHolder.id = event.id

        return theView
    }

    override fun getItem(i: Int) = records[i]

    override fun getItemId(i: Int) = 1L

    override fun getCount() = records.size
}

data class EventViewHolder(
        val nameView: TextView,
        val dateView: TextView
) {
    var id: String? = null
}