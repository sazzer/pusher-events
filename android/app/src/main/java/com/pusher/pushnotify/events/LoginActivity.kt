package com.pusher.pushnotify.events

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onClickLogin(v: View) {
        val usernameInput = findViewById<EditText>(R.id.userNameInput)
        val username = usernameInput.text.toString()
        if (username.isBlank()) {
            Toast.makeText(this, "No username entered!", Toast.LENGTH_LONG).show()
        } else {
            (this.application as EventsApplication).username = username
            startActivity(Intent(this, EventsListActivity::class.java))
        }
    }
}
