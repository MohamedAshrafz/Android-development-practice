package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.guesstheword.BuzzerUtil

class GameViewModel : ViewModel() {

    // The current word
    // for encapsulation we use immutable LiveData to access the private fields
    private val _word = MutableLiveData<String>("")
    val word: LiveData<String>
        get() = _word

    // The current score
    // for encapsulation we use immutable LiveData to access the private fields
    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    // trigger the game finished event
    // for encapsulation we use immutable LiveData to access the private fields
    private val _eventGameFinished = MutableLiveData<Boolean>(false)
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    // current rime
    // for encapsulation we use immutable LiveData to access the private fields
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val _eventOnBuzzing = MutableLiveData<BuzzerUtil.BuzzType>(BuzzerUtil.BuzzType.NO_BUZZ)
    val eventOnBuzzing: LiveData<BuzzerUtil.BuzzType>
        get() = _eventOnBuzzing

//    private val _eventOnSkip = MutableLiveData<Boolean>(false)
//    val eventOnSkip: LiveData<Boolean>
//        get() = _eventOnSkip

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L

        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L

        // This is the total time of the game
        const val COUNTDOWN_TIME = 60000L
    }

    private lateinit var timer: CountDownTimer

    init {
        // Log.i("GameViewModel", "GameViewModel was created")

        resetList()
        nextWord()

        _currentTime.value = COUNTDOWN_TIME / ONE_SECOND

//        timer = object : CountDownTimer(COUNTDOWN_TIME, 1000L) {
//            override fun onTick(millisUntilFinished: Long) {
//                _currentTime.value = (millisUntilFinished / ONE_SECOND)
//                Log.i("GameFragmentModel", "current time: ${_currentTime.value}")
//            }
//
//            override fun onFinish() {
//                _eventGameFinished.value = true
//            }
//        }
    }

    fun startTimer() {
        Log.i("GameFragmentView", "onStart")
        timer = object : CountDownTimer(
            (currentTime.value ?: 0) * ONE_SECOND,
            ONE_SECOND
        ) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("GameFragmentModel in re", "current time: ${_currentTime.value}")
                _currentTime.value = (millisUntilFinished / ONE_SECOND)

                if (currentTime.value!! < 10L) {
                    onPanic()
                }
            }

            override fun onFinish() {
                _eventGameFinished.value = true
                _eventOnBuzzing.value = BuzzerUtil.BuzzType.GAME_OVER
            }
        }
        timer.start()
    }

    fun stopTimer() {
        Log.i("GameFragmentView", "onStop")
        timer.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup"
//            "calendar",
//            "sad",
//            "desk",
//            "guitar",
//            "home",
//            "railway",
//            "zebra",
//            "jelly",
//            "car",
//            "crow",
//            "trade",
//            "bag",
//            "roll",
//            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        } else {
            _word.value = wordList.removeAt(0)
        }
    }


    /** used on correct button
     * decrement the score and move to the next word **/
    fun onCorrect() {
        _score.value = _score.value?.plus(1)
        _eventOnBuzzing.value = BuzzerUtil.BuzzType.CORRECT
        nextWord()
    }

    /** used on skip button
     * increment the score and move to the next word **/
    fun onSkip() {
        _score.value = _score.value?.minus(1)
        _eventOnBuzzing.value = BuzzerUtil.BuzzType.NO_BUZZ
        nextWord()
    }

    fun clearBuzzing() {
        _eventOnBuzzing.value = BuzzerUtil.BuzzType.NO_BUZZ
    }

    fun onPanic() {
        _eventOnBuzzing.value = BuzzerUtil.BuzzType.COUNTDOWN_PANIC
    }

    fun clearGameFinished() {
        _eventGameFinished.value = false
    }
}