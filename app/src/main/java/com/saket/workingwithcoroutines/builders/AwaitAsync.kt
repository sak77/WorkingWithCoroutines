package com.saket.workingwithcoroutines.builders

import kotlinx.coroutines.*

/**
 * Await-async is similar to launch Coroutine builder, in that
 * it is an extension to CoroutineScope class. It inherits the
 * CoroutineContext from the CoroutineScope.
 *
 * However, it returns a future result as a instance of
 * Deferred<T>. Deferred is a non-blocking cancellable future
 * object which acts as a proxy for a result that is initially
 * unknown.
 *
 * await() is a suspending function that is called upon the async
 * builder to fetch the value of the Deferred object that is
 * returned. The coroutine started by async will be suspended
 * until the result is ready. When the result is ready,
 * it is returned and the coroutine resumes.
 */

/*
When creating a coroutine from a non-coroutine, start with
launch. That way, if they throw an uncaught exception it'll
automatically be propagated to uncaught exception handlers
(which by default crash the app).

A coroutine started with async won't throw an exception to its
caller until you call await. However, you can only call await
from inside a coroutine, since it is a suspend function.
 */

val a = 10
val b = 20

fun testAwaitAsyncWithPredefinedScope() {
    println("Saket Gone to calculate sum of a & b")

    GlobalScope.launch {
        val result = async {
            calculateSum()
        }
        println("Saket Sum of a & b is: ${result.await()}")
    }
    println("Saket Carry on with some other task while the coroutine is waiting for a result...")
    Thread.sleep(3000L) // keeping jvm alive till calculateSum is finished
}

/*
Notice how i am using the coroutineScope instance to launch 2 separate Coroutines.
First i use async to calculate the Sum. Then i launch another Coroutine to display
the result. It is observed that both coroutines execute on separate threads from the
default thread-pool....
Btw i have not used the cancel here as it was not necessary..
 */
fun asyncWithCoroutineScope() {
    val customScope2 = CoroutineScope(Dispatchers.IO)
    val result = customScope2.async {
        delay(1000)
        println("Saket Calculating sum in async")
        calculateSum()
    }

    customScope2.launch {
        println("Saket Sum is ${result.await()}")
    }
}

/*
We can use async coroutine builder to call multiple pieces of
asynchronous code in parallel. We can then combine the results
from both to display a combined result. This leads
to lower latency and in extension, a superior user experience.
 */
fun useAsyncToCallMultipleAsyncCoroutines() {
    runBlocking {
        val firstResult: Deferred<Boolean> = async {
            isFirstCriteriaMatch()
        }

        val secondResult: Deferred<Boolean> = async {
            isSecondCriteriaMatch()
        }

        if (firstResult.await() && secondResult.await()) {
            println("Saket criteria matched, go ahead!")
        } else {
            println("Saket criteria unmatched, sorry!")
        }
    }
}

suspend fun isFirstCriteriaMatch(): Boolean {
    delay(1000L) // simulate long running task
    return true
}

suspend fun isSecondCriteriaMatch(): Boolean {
    delay(1000L)
    return false
}

suspend fun calculateSum(): Int {
    delay(2000L)
    return a + b
}
