package com.saket.workingwithcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saket.workingwithcoroutines.scopes.launchCoroutineWhenLifecycleResumed
import com.saket.workingwithcoroutines.scopes.lifecycleAwareFlowCollection
import com.saket.workingwithcoroutines.scopes.repeatOnLifeCycleStart
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //asyncWithCoroutineScope()
        launchCoroutineWhenLifecycleResumed(this)

        /*
        testDummyNetworkCallWithLaunch { response ->
            println("Saket response $response")
        }
         */

        /*
        Suspend functions and regular functions launched by a
        CoroutineScope, behave in a similar manner.
        println("Saket calling myscope from ${Thread.currentThread().name}")
        val myscope = CoroutineScope(Dispatchers.Main)
        myscope.launch {
            setTextAfterDelay("")
        }
        println("Saket after myscope")
         */
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
        println("Saket current thread is ${Thread.currentThread().name}")
        //Thread.sleep(5000)
        delay(2000)
        println("Saket saying $sayHello")
    }

    override fun onStart() {
        super.onStart()
        println("Saket MainActivity started")
    }

    override fun onStop() {
        super.onStop()
        println("Saket MainActivity stopped")
    }
}
