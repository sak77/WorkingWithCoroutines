package com.saket.workingwithcoroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
Created by sshriwas on 2020-10-10
 */
class MyViewModel : ViewModel() {
    init {
    }

    fun launchCoroutine() {
        //A viewModelScope is defined for each viewmodel in the app.
        //Any coroutine launched in this scope is automatically cancelled when the viewmodel is cleared...
        viewModelScope.launch {
            for (i in 1..200) {
                delay(1000)
                Log.v("MyViewModel", "Current value $i")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.v("MyViewModel", "onCleared called")
    }
}