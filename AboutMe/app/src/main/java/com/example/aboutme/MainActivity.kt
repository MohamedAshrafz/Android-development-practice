package com.example.aboutme

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.aboutme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val myName = MyName(name = "Mohamed Ashraf")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // normal layout inflation setContentView
        // setContentView(R.layout.activity_main)
        // setContentView using binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // using find by id method
        // val doneButton = findViewById<Button>(R.id.done_button)

        // using view binding (which is better method)
        binding.myName = myName
        binding.doneButton.setOnClickListener {
            doneEnteringNickname(it)
        }
    }

    private fun doneEnteringNickname(view: View) {

        binding.apply {
            // nicknameTextView.text = nicknameEditText.text
            // notice nicknameEditText.text is of type Editable
            myName?.nickname = nicknameEditText.text.toString()
            nicknameEditText.visibility = View.GONE
            view.visibility = View.GONE
            nicknameTextView.visibility = View.VISIBLE
            println(nicknameEditText.text)
            invalidateAll()
        }

        // Code for Hiding the KeyBoard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}