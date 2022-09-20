/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    private fun rotater() {

        // get the current rotation of the view
        val startAngle = star.rotation

        // make the view rotate 360 from the starting rotation of the view
        // this can handle multiple presses in the rotate button
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, startAngle, startAngle + 360)

        animator.duration = 1000

//        // to disable the rotation button while still animating the previous rotation
//        animator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationStart(animation: Animator) {
//                rotateButton.isEnabled = false
//            }
//
//            override fun onAnimationEnd(animation: Animator) {
//                rotateButton.isEnabled = true
//            }
//        })

        animator.start()
    }

    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 0f, 200f)

        // number of moves after the first animation
        animator.repeatCount = 1
        // can be restart
        animator.repeatMode = ObjectAnimator.REVERSE

        animator.disableButtonDuringAnimation(translateButton)
        animator.start()
    }

    private fun scaler() {
    }

    private fun fader() {
    }

    private fun colorizer() {
    }

    private fun shower() {
    }

}

// extension function to do the disabling for us(kotlin like)
private fun ObjectAnimator.disableButtonDuringAnimation(button: View) {
    addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                button.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                button.isEnabled = true
            }
        })
}