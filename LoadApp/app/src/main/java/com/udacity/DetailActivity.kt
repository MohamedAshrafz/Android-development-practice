package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancel(MainActivity.NOTIFICATION_ID)

        val nameTextView = findViewById<TextView>(R.id.name_textView)
        val statusTextView = findViewById<TextView>(R.id.status_textView)

        val nameText = intent.getStringExtra(MainActivity.NAME)
        val statusText = intent.getStringExtra(MainActivity.STATUS)

        nameTextView.text = nameText
        statusTextView.text = statusText

        val okButton = findViewById<Button>(R.id.ok_button)

        okButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
