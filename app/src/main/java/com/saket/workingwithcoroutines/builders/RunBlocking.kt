package com.saket.workingwithcoroutines.builders

import android.view.View
import kotlinx.coroutines.*

/**
 * Runs a new coroutine and blocks the current thread
 * until its completion.
 *
 * It is designed to bridge regular blocking code to libraries
 * that are written in suspending style, to be used in
 * main functions and in tests.
 *
 * This function should not be used from a coroutine.
 */

/*
runBlocking by default runs with coroutine scope of
the current thread's event loop.

However, rotating device, causes the activity to be destroyed and
recreated. But this does not seem to affect the operation of
the runBlocking function. But since it is runBlocking, the current
Activity is unresponsive while the coroutine inside runBlocking is
running. For eg. clicking on the back button does not close the
activity.
 */
fun testRunBlockingWithDefaultCoroutineScope() {
    println("Saket is calling a runBlocking function")
    /*
    launch and async functions are extensions to CoroutineScope,
    they inherit CoroutineContext provided by the CoroutineScope.
    But since runBlocking does not extend from CoroutineScope, so
    it does return a job that can be used to cancel its operation.

    If this blocked thread is interrupted (see Thread.interrupt),
    then the coroutine job is cancelled and this runBlocking
    invocation throws InterruptedException.

    So Thread.interrupt on the current thread seems to be the only
    way to cancel a runBlocking operation.
     */
    runBlocking {
        countTo10()
    }

    println("Saket finished calling a runBlocking function")
}

/*
We can provide our own coroutineContext which
provides a coroutineDispatcher. when coroutineDispatcher
is specified, the coroutine runs in the scope of the
dispatcher.
 */
fun testRunBlockingWithCustomCoroutineScope() {
    println("Saket calling runBlocking with custom context")
    /*
    Despite calling runBlocking on Dispatchers.IO it
    still blocks the current thread while executing
    the coroutine.
     */
    runBlocking(Dispatchers.IO) {
        countTo10()
    }
    println("Saket done calling runBlocking with custom context")
}

fun countTo10() {
    for (i in 0..10) {
        println("Saket counting to $i on ${Thread.currentThread().name}")
        Thread.sleep(1000)
    }
}

//This is just a trial...should not be used..
fun testRunBlockingInsideCoroutine(view: View) {
    //runBlocking should NOT be called from a coroutine.
    //As seen below, it gives a warning - inappropriate blocking method call.
    //Since it tries to block the default dispatcher thread maybe or is not effective in
    //blocking the main thread as it is intended....
    CoroutineScope(Dispatchers.Main).launch {
        runBlocking {
            println("Running runBlocking inside CoroutineScope.launch")
        }
    }
}
