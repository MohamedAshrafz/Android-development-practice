package com.udacity.shoestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class MainActivityViewModel : ViewModel() {

    private var _shoeList = MutableLiveData(mutableListOf<Shoe>())
    val shoeList: LiveData<MutableList<Shoe>>
        get() = _shoeList

    val name = MutableLiveData<String>()

    val size = MutableLiveData<String>()

    val company = MutableLiveData<String>()

    val description = MutableLiveData<String>()

    private val _eventOnSave = MutableLiveData<Boolean>()
    val eventOnSave: LiveData<Boolean>
        get() = _eventOnSave

    init {
        addInitialValues()
    }

    private fun addInitialValues() {
        // dummy data
        _shoeList.value?.apply {
            add(Shoe("shoeA", 26.0, "comA", "normal shoe"))
            add(Shoe("shoeB", 27.0, "comB", "medium shoe"))
            add(Shoe("shoeC", 40.0, "comC", "large shoe"))
            add(Shoe("shoeD", 50.0, "comD", "very large shoe"))
            add(Shoe("shoeE", 20.0, "comE", "very very large shoe"))
            add(Shoe("shoeBA", 26.0, "comBA", "normal shoe"))
            add(Shoe("shoeBB", 27.0, "comBB", "medium shoe"))
            add(Shoe("shoeBC", 40.0, "comBC", "large shoe"))
            add(Shoe("shoeBD", 50.0, "comBD", "very large shoe"))
            add(Shoe("shoeBE", 20.0, "comBE", "very very large shoe"))
        }
    }

    fun addShoe() {
        val newShoe = Shoe(
            name.value.toString(),
            size.value.toString().toDouble(),
            company.value.toString(),
            description.value.toString()
        )
        _shoeList.value?.add(newShoe)
    }

    fun validate(): Boolean {
        if (name.value.isNullOrEmpty() ||
            size.value.isNullOrEmpty() ||
            company.value.isNullOrEmpty() ||
            description.value.isNullOrEmpty()
        ) {
            return false
        }
        return true
    }

    fun setOnSave() {
        _eventOnSave.value = true
    }

    fun clearOnSave() {
        _eventOnSave.value = false
        name.value = null
        size.value = null
        company.value = null
        description.value = null
    }

}