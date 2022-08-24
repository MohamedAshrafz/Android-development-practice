package com.example.android.guesstheword.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScoreArgument: Int) : ViewModel() {
    // finalScore LiveData
    private val _finalScore = MutableLiveData<Int>(finalScoreArgument)
    val finalScore: LiveData<Int>
        get() = _finalScore

    private val _playAgain = MutableLiveData<Boolean>(false)
    val playAgain: LiveData<Boolean>
        get() = _playAgain


    fun setPlayAgain(){
        _playAgain.value = true
    }

    fun clearPlayAgain(){
        _playAgain.value = false
    }
}