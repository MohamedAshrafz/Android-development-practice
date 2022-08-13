package com.example.colormyviews

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.colormyviews.databinding.ActivityMainBinding
// import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var states: MutableList<Boolean>
    // private lateinit var colorsForBoxes: List<Int>
    private var lastBox: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        states = MutableList(7) { false }


        setListeners()
    }

    private fun setListeners() {
        val clickableViews: List<View> = listOf(
            binding.boxOneTextView,
            binding.boxTwoTextView,
            binding.boxThreeTextView,
            binding.boxFourTextView,
            binding.boxFiveTextView,
            binding.constraintLayout,
            binding.redButton,
            binding.yellowButton,
            binding.greenButton
        )

//        val clickableViews: List<View> = listOf(
//            box_one_textView,
//            box_two_textView,
//            box_three_textView,
//            box_four_textView,
//            box_five_textView,
//            constraint_layout,
//            red_button,
//            yellow_button,
//            green_button
//        )


        for (clickableV in clickableViews) {
            clickableV.setOnClickListener {
                viewColor(it)
            }
        }
    }

    private fun viewColor(view: View) {
        // without switching using binding
//        when (view.id) {
//            binding.boxOneTextView.id -> view.setBackgroundColor(Color.DKGRAY)
//            binding.boxTwoTextView.id -> view.setBackgroundColor(Color.GRAY)
//
//            binding.boxThreeTextView.id -> view.setBackgroundResource(android.R.color.holo_green_light)
//            binding.boxFourTextView.id -> view.setBackgroundResource(android.R.color.holo_green_dark)
//            binding.boxFiveTextView.id -> view.setBackgroundResource(android.R.color.holo_blue_dark)
//            binding.redButton.id -> binding.boxThreeTextView.setBackgroundResource(R.color.myRed)
//            binding.yellowButton.id -> binding.boxFourTextView.setBackgroundResource(R.color.myYellow)
//            binding.greenButton.id -> binding.boxFiveTextView.setBackgroundResource(R.color.myGreen)
//
//            else -> view.setBackgroundColor(Color.LTGRAY)
//        }
//        binding.invalidateAll()

        // using extensions (deprecated)
//        when (view.id) {
//            box_one_textView.id -> view.setBackgroundColor(Color.DKGRAY)
//            box_two_textView.id -> view.setBackgroundColor(Color.GRAY)
//
//            box_three_textView.id -> view.setBackgroundResource(android.R.color.holo_green_light)
//            box_four_textView.id -> view.setBackgroundResource(android.R.color.holo_green_dark)
//            box_five_textView.id -> view.setBackgroundResource(android.R.color.holo_blue_dark)
//            red_button.id -> box_three_textView.setBackgroundResource(R.color.myRed)
//            yellow_button.id -> box_four_textView.setBackgroundResource(R.color.myYellow)
//            green_button.id -> box_five_textView.setBackgroundResource(R.color.myGreen)
//
//            else -> view.setBackgroundColor(Color.LTGRAY)
//        }

        // using binding + make them switching btw colored and uncolored
        when (view.id) {
            binding.boxOneTextView.id -> {
                if (!states[1]) view.setBackgroundColor(Color.DKGRAY)
                else view.setBackgroundColor(Color.WHITE)
                states[1] = states[1].not()
                lastBox = view
            }
            binding.boxTwoTextView.id -> {
                if (!states[2]) view.setBackgroundColor(Color.GRAY)
                else view.setBackgroundColor(Color.WHITE)
                states[2] = states[2].not()
                lastBox = view
            }
            binding.boxThreeTextView.id -> {
                if (!states[3]) view.setBackgroundResource(android.R.color.holo_green_light)
                else view.setBackgroundColor(Color.WHITE)
                states[3] = states[3].not()
                lastBox = view
            }
            binding.boxFourTextView.id -> {
                if (!states[4]) view.setBackgroundResource(android.R.color.holo_green_dark)
                else view.setBackgroundColor(Color.WHITE)
                states[4] = states[4].not()
                lastBox = view
            }
            binding.boxFiveTextView.id -> {
                if (!states[5]) view.setBackgroundResource(android.R.color.holo_blue_light)
                else view.setBackgroundColor(Color.WHITE)
                states[5] = states[5].not()
                lastBox = view
            }

            binding.redButton.id -> {
                lastBox?.setBackgroundResource(R.color.myRed)
            }

            binding.yellowButton.id -> {
                lastBox?.setBackgroundResource(R.color.myYellow)
            }

            binding.greenButton.id -> {
                lastBox?.setBackgroundResource(R.color.myGreen)
            }

            else -> {
                if (!states[6]) view.setBackgroundColor(Color.LTGRAY)
                else view.setBackgroundColor(Color.WHITE)
                states[6] = states[6].not()
                lastBox = view
            }
        }
        binding.invalidateAll()
    }

    private fun switchColors(elementNumber: Int, view: View) {
        if (!states[elementNumber]) view.setBackgroundColor(Color.DKGRAY)
        else view.setBackgroundColor(Color.WHITE)
        states[elementNumber] = states[elementNumber].not()
    }
}
