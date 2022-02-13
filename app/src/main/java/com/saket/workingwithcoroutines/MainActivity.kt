package com.saket.workingwithcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.saket.workingwithcoroutines.builders.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

/**
 *
 * This app takes a closer look at coroutines.
 *
 * Coroutines have 3 major parts:
 * Coroutine Builders - launch, runBlocking, async-await
 * Coroutine Context - Job + Dispatcher
 * Coroutine Scopes -
 * ViewModelScope
 * LifeCycleScope
 * GlobalScope
 * MainScope
 * CoroutineScope
 */
class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        asyncWithCoroutineScope()
    }

    /*
    Suspend functions will suspend execution of the calling coroutine/function
    while they execute.
    Suspend can only be called by other suspend functions or Coroutines.
    Coroutines can call suspend as well regular functions.
    Suspended functions can call public suspended functions like:
    coroutineScope, delay and withContext.
    As a rule of thumb, use regular function with coroutines unless the compiler
    asks you to use 'suspend' keyword.
     */
    suspend fun setTextAfterDelay(sayHello: String) {
        /*
        Delay is a suspend function. So it can only be called from another suspend function.
        So add suspend modifier to the calling function
         */
        delay(2000)
        textView.setText(sayHello)
    }

    //We can call regular functions from coroutines
    private fun showMessage(string_message: String) {
        Log.v(TAG, string_message + " in " + Thread.currentThread().name)
    }
}