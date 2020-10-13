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
            //Executes on main thread by default...
            //But this does not seem to affect the UI. Which is still responsive?
            //The answer to this can be found here -
            //https://stackoverflow.com/questions/60273519/how-can-coroutinescopejobdispatchers-main-run-on-the-main-ui-thread
            //Basically, the below code posts messages to the event loop of the main thread.
            //Also Coroutines are non-blocking, which means they allow other operations to
            //continue to execute while they are suspended. Once the message is executed by the
            //looper in the main thread, the coroutine resumes its execution.
            Log.v("MyViewModel", "Launch viewmodelscope coroutine on thread - " + Thread.currentThread().name)
            for (i in 1..20) {
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