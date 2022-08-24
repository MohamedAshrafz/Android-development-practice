/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.BuzzerUtil
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameViewModelWord: LiveData<String>
    private lateinit var gameViewModelScore: LiveData<Int>

    private lateinit var binding: GameFragmentBinding
    lateinit var buzzerUtil: BuzzerUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater, R.layout.game_fragment, container, false
        )

        // Log.i("GameFragment", "ViewModelProvider was called")

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        binding.viewModel = gameViewModel
        binding.lifecycleOwner = this

        // replaced with the data binding directly in the xml file
        // getting the livedata instances and set its observers
//        gameViewModelWord = gameViewModel.word
        gameViewModelScore = gameViewModel.score

        // buzzer initialization
        buzzerUtil = BuzzerUtil(requireActivity())

//        gameViewModelWord.observe(viewLifecycleOwner, Observer { newWord ->
//            // just for providing the show of wrong data
//            if (!newWord.equals("null"))
//                binding.wordText.text = newWord
//        })
//        gameViewModelScore.observe(viewLifecycleOwner, Observer { newScore ->
//            binding.scoreText.text = newScore.toString()
//        })

        gameViewModel.eventGameFinished.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) {
                gameFinished()
                gameViewModel.clearGameFinished()
            }
        })
//        gameViewModel.currentTime.observe(viewLifecycleOwner, Observer{ newTime ->
//            binding.timerText.text = DateUtils.formatElapsedTime(newTime)
//            // Toast.makeText(context,"the current tick is $newTime", Toast.LENGTH_SHORT).show()
//        })

//        binding.correctButton.setOnClickListener {
//            gameViewModel.clearOnCorrect()
//        }
//        binding.skipButton.setOnClickListener {
//            gameViewModel.onSkip()
//        }

        gameViewModel.eventOnBuzzing.observe(viewLifecycleOwner, Observer { buzzType ->
            if (!buzzType.pattern.contentEquals(BuzzerUtil.BuzzType.NO_BUZZ.pattern)) {
                buzzerUtil.buzz(buzzType.pattern)
                gameViewModel.clearBuzzing()
            }
        })

        return binding.root
    }

    override fun onStart() {
        Log.i("GameFragment", "onStart")
        super.onStart()
        gameViewModel.startTimer()
    }

    override fun onStop() {
        Log.i("GameFragment", "onStop")
        super.onStop()
        gameViewModel.stopTimer()
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val action =
            GameFragmentDirections.actionGameFragmentToScoreFragment(gameViewModelScore.value ?: 0)
        findNavController(this).navigate(action)
        // Toast.makeText(context, "game finished", Toast.LENGTH_SHORT).show()
    }
}
