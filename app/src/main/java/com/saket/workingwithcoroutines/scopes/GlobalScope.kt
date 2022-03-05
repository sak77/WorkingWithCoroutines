package com.saket.workingwithcoroutines.scopes

import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope

const val TAG = "GlobalScope"

/**
 * GlobalScope is used to launch instance of
 * coroutine which runs in the app context.
 * It is not bound to any job. So there is no handle
 * on it to cancel it prematurely.
 * Active coroutines launched in globalscope do not
 * keep the process alive, instead they are like daemon
 * threads.
 * It is easy to accidentally create a memory leak using
 * globalscope.
 *
 * There are limited legitimate use cases where globalscope
 * can be used:
 * top-level background processes which must remain active
 * for the entire lifecycle of the app.
 */

fun launchGlobalScopeWithParams(view: View) {
    //You launch in GlobalScope with some additional params -
    GlobalScope.launch(context = Dispatchers.Main, block = {
        delay(3000)
        showMessage("Hello Again!")
    }, start = CoroutineStart.DEFAULT)
}

fun asyncGlobalScopeWithNoParams(view: View) {
    GlobalScope.launch {
        val result = async { calculateSum(3, 4) }
        //delay(2000)
        Log.v(TAG, "Result of sum method: ${result.await()}")
        Log.v(TAG, "Continue with coroutine execution.")
    }
    Log.v(TAG, "Doing some work outside coroutine.")
}


fun calculateSum(a: Int, b: Int): Int {
    Thread.sleep(3000)  //Introduce delay for 3 secs to mock some work
    return a + b
}

/*
Here we execute 3 async coroutines inside the global scope. Each coroutine operates
using a different coroutineContext. And the final result is displayed in the logs.
 */
fun asyncWithGlobalScopeAndContext(view: View): Unit {
    GlobalScope.launch {
        val result1 = async(coroutineContext, CoroutineStart.DEFAULT) {
            showMessage("Calculating sum using scope context")
            delay(2000)
            calculateSum(5, 7)
        }

        val result2 = async(Dispatchers.Main, CoroutineStart.DEFAULT) {
            showMessage("Calculating diff using Dispatchers.Main")
            delay(4000)
            calculateDiff(9, 3)
        }
        val result3 = async(Dispatchers.Default, CoroutineStart.DEFAULT) {
            showMessage("Calculating product using Dispatchers.Default")
            delay(500)
            calculateDProduct(3, 9)
        }
        //The final result gets printed when all 3 async corotuines return their results
        Log.v(
            TAG,
            "Result of async operation : ADD: ${result1.await()} , Diff: ${result2.await()}, Product: ${result3.await()} "
        )
    }
}

fun calculateDiff(a: Int, b: Int) = a - b

fun calculateDProduct(a: Int, b: Int) = a * b

/*
Lazily started Coroutine.
async can be made lazy by setting its start parameter to CoroutineStart.LAZY.
In this mode it only starts the coroutine when its result is required by await,
or if its Job 's start function is invoked.
 */
suspend fun lazyAsyncCoroutine() {
    val one = GlobalScope.async(start = CoroutineStart.LAZY) {
        delay(2000)
        32
    }
    val two = GlobalScope.async(start = CoroutineStart.LAZY) {
        delay(2300)
        23
    }

    //Start one
    one.start()
    //Start two
    two.start()

    Log.d(TAG, "The answer is ${one.await() + two.await()}")
    /*
    Note that if we just call await in println without first calling start on
    individual coroutines, this will lead to sequential behavior, since await starts
    the coroutine execution and waits for its finish, which is not the intended use-case
    for laziness. The use-case for async(start = CoroutineStart.LAZY) is a replacement
    for the standard lazy function in cases when computation of the value involves
    suspending functions.
     */
}


//Can we change Dispatcher of a coroutine after it has started? Use withContext()
//Can we launch multiple coroutines in different scopes? Probably yes..
fun launchGlobalScopeWithNoParams(view: View) {
    //Using Global Scope is generally discouraged as in this case the coroutine
    //is alive throughout the lifetime of the app. It holds memory which may not
    // be required and hence it is inefficient.
    GlobalScope.launch {
        showMessage("Launching coroutine in ${Thread.currentThread().name}")
        //Use withContext to switch to Main thread.
        withContext(Dispatchers.Main) {
            delay(3000) //wait for 3 seconds
            showMessage("Coroutine shows message on thread ${Thread.currentThread().name}")
        }
    }
}


//We can call regular functions from coroutines
private fun showMessage(string_message: String) {
    Log.v(TAG, string_message + " in " + Thread.currentThread().name)
}
