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

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

//    private lateinit var a1: Bitmap
//    private lateinit var a2: Bitmap
//    private lateinit var a3: Bitmap
//    private lateinit var a4: Bitmap
//    private lateinit var a5: Bitmap
//    private lateinit var a6: Bitmap
//    private lateinit var a7: Bitmap
//    private lateinit var a8: Bitmap
//    private lateinit var a9: Bitmap
//    private lateinit var a10: Bitmap
//    private lateinit var a11: Bitmap
//
//    private lateinit var list: List<Bitmap>

    private lateinit var starBitmap: Bitmap

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

        // NOTICE: you cannot put an xml file into a Bitmap directly
        // that's why we must get the drawable from the recourses then we can set its bounds
        // then we can draw on the Bitmap using a canvas object
        val drawable: Drawable? = ContextCompat.getDrawable(applicationContext, R.drawable.ic_star)

        if (drawable != null) {
            // NOTICE: YOU MUST SET THE BOUNDS TO BOTH THE BITMAP AND THE DRAWABLE
            drawable.setBounds(
                0, 0,
                drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
            starBitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(starBitmap)
            drawable.draw(canvas)
        }


//        a1 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a1)
//        a2 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a2)
//        a3 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a3)
//        a4 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a4)
//        a5 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a2)
//        a6 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a6)
//        a7 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a7)
//        a8 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a8)
//        a9 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a9)
//        a10 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a10)
//        a11 = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.a11)
//
//        list = listOf(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)

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


        animator.apply {
            // number of moves after the first animation
            repeatCount = 1
            // can be restart
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(translateButton)
            start()
        }
    }

    private fun scaler() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        animator.apply {
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(scaleButton)
            start()
        }
    }

    private fun fader() {
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        animator.apply {
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(fadeButton)
            start()
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun colorizer() {
        val frame = FrameLayout(applicationContext)

        val animator = ObjectAnimator.ofArgb(
            star.parent,
            "backgroundColor",
            Color.BLACK,
            Color.RED
        )

        animator.apply {
            duration = 500
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableButtonDuringAnimation(colorizeButton)
            start()
        }
    }


    private fun shower() {
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW: Float
        var starH: Float


        for (i in 1..4) {
            // making a new star
            val newStar = AppCompatImageView(this)

            // newStar.setImageBitmap(list[(Math.random() * list.size).toInt()])
            newStar.setImageBitmap(starBitmap)

            // resetting the initial value of the starW and starH
            starW = starBitmap.width.toFloat()
            starH = starBitmap.height.toFloat()


            // setting its layoutParams to the parent view
            newStar.layoutParams = FrameLayout.LayoutParams(
                starW.toInt(),
                starH.toInt()
            )
            newStar.scaleX = Math.random().toFloat() * 1.5f + 0.1f
            newStar.scaleY = newStar.scaleX

            // calculating the new dimensions of the newStar the absolute dimension = scale*oldDimension
            starW *= newStar.scaleX
            starH *= newStar.scaleY

            // making the newStar goes in the random position [-starW*(2/3), containerW-(starW*(2/3))]
            newStar.translationX = (Math.random().toFloat() * containerW) - (starW * 1 / 2)

            // mover
            val mover =
                ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH, containerH + starH)
            // make the star accelerate while falling down with constant acceleration
            // this is using interpolator property and AccelerateInterpolator class
            mover.interpolator = AccelerateInterpolator(1f)

            // rotator
            val rotator = ObjectAnimator.ofFloat(
                newStar,
                View.ROTATION,
                // make the star rotate random number of rotations
                (3 * 360) * Math.random().toFloat()
            )
            // make the rotation with constant speed
            rotator.interpolator = LinearInterpolator()

            // put all the animation in parallel set
            val set = AnimatorSet()
            set.playTogether(mover, rotator)
            set.duration = (Math.random().toFloat() * 5000 + 500).toLong()

            // remove the star if the animation ended
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    container.removeView(newStar)
                }
            })
            container.addView(newStar)
            set.start()
        }
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
