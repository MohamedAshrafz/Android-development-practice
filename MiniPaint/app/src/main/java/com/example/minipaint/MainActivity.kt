package com.example.minipaint

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val myCanvas = MyCanvasView(this)
//        myCanvas.contentDescription = getString(R.string.canvasContentDescription)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.clear_button).setOnClickListener {
            findViewById<MyCanvasView>(R.id.myCanvasView).clearScreen()
        }
    }
}