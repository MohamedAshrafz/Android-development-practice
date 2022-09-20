package com.example.customfancontroller

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dialView = findViewById<DialView>(R.id.dialView)
        val customViewLabel = findViewById<TextView>(R.id.customViewLabel)
        var speed = 0
        dialView.fanSpeed = speed



        customViewLabel.text = dialView.fanSpeed.toString()

        findViewById<Button>(R.id.switch1_button).setOnClickListener {
            speed = when (speed) {
                0 -> 1
                1 -> 2
                2 -> 3
                else -> 0
            }
            dialView.fanSpeed = speed
            customViewLabel.text = dialView.fanSpeed.toString()
        }

    }
}