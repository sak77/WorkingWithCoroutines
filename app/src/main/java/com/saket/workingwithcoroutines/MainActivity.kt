package com.saket.workingwithcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.saket.workingwithcoroutines.scopes.launchCoroutineWhenLifecycleResumed
import com.saket.workingwithcoroutines.scopes.lifecycleAwareFlowCollection
import com.saket.workingwithcoroutines.scopes.repeatOnLifeCycleStart
import kotlinx.coroutines.*

/**
 *
 * All Coroutines run within a CoroutineScope:
 * Android provides some scope instances tied to various lifecycle components -
 * ViewModelScope (For ViewModel)
 * LifeCycleScope (For Activity/Fragment/LifecycleService)
 * GlobalScope (App level)
 * MainScope (Runs task on Main Thread)
 *
 * Or, devs can create their own CoroutineScope using CoroutineScope instance
 * Coroutine Builders are used to start a Coroutine -
 * launch, runBlocking, async-await
 * Coroutine Context - immutable list of Elements -
 * Job + Dispatcher
 */
class MainActivity : AppCompatActivity() {
    private lateinit var myscope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //asyncWithCoroutineScope()
        //launchCoroutineWhenLifecycleResumed(this)

        /*
        testDummyNetworkCallWithLaunch { response ->
            println("Saket response $response")
        }
         */

        val btnStart = findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            /*
    Suspend functions and regular functions launched by a
    CoroutineScope, behave in a similar manner.
     */
            println("Saket calling myscope from ${Thread.currentThread().name}")
            myscope = CoroutineScope(Dispatchers.Main)
            myscope.launch {
                setTextAfterDelay("hej!")
            }
            println("Saket after myscope")
        }
        /*
        All Coroutines execute within a CoroutineScope. The CoroutineScope,
        determines the lifetime of the coroutine. Here the coroutine is launched
        within the Activity's lifecycleScope. Which means the execution is tied
        to the Activity's lifecycle. If the activity's lifecycle is ended,
        then the coroutine execution should also stop etc.
        */
        lifecycleScope.launch {}
    }

    /*
    Coroutines can call suspend as well regular functions.
    Suspended functions can call public suspended functions like:
    coroutineScope, delay and withContext.
    As a rule of thumb, use regular function with coroutines unless the compiler
    asks you to use 'suspend' keyword.
     */
    private suspend fun setTextAfterDelay(sayHello: String) {/*
        Delay is a suspend function. So it can only be called from another suspend function.
        So add suspend modifier to the calling function
         */
        repeat(10) {
            println("Saket current thread is ${Thread.currentThread().name}")
            //Thread.sleep(5000)
            delay(2000)
        }
        println("Saket saying $sayHello")
    }

    override fun onStart() {
        super.onStart()
        println("Saket MainActivity started")
    }

    override fun onStop() {
        super.onStop()
        println("Saket MainActivity stopped")/*
        With Custom CoroutineScope, the dev is responsible for
        sync the operation with component lifecycle.
         */
        myscope.cancel()
    }
}
