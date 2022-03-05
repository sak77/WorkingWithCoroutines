package com.saket.workingwithcoroutines.builders

import kotlinx.coroutines.*

/**
 * launch function starts a new coroutine WITHOUT BLOCKING the
 * current thread. It also returns a reference to the coroutine
 * as a Job object. The coroutine is cancelled when the job is
 * cancelled.
 */

const val COUNTDOWN_VAL = 20
/*
Unlike runBlocking, launch and await are extension functions of
CoroutineScope class. They inherit the CoroutineContext provided by
the CoroutineScope.

runBlocking vs launch
- RB blocks current thread, but launch does not.
- RB uses Dispatcher.MAIN for execution but launch/await
uses CoroutineContext (Dispatcher) provided by the CoroutineScope.
- RB does not return a job instance to control the coroutine,
but launch provides it.
 */

/*
CoroutineScope can be used to control lifecycle/lifetime of the
coroutine. CoroutineScope provides methods like start, cancel.

Android provides CoroutineScope out-of-the-box such as LifecycleScope
or ViewmodelScope which are tied to lifecycle of UI controllers
or Viewmodel.

But otherwise, the CoroutineScope is not tied to the lifecycle of
a UI component. Then one must manually call start/cancel from
lifecycle event callbacks to make the coroutine behave according to
the lifecycle.
 */
fun launchCoroutineFromGlobalScope() {
    println("Saket calling GlobalScope.launch")
    val globalJob = GlobalScope.launch {
        countDown()
    }
    println("Saket after GlobalScope.launch")
}

fun cancelCoroutineAfter3secs() {
    println("Saket calling GlobalScope.launch")
    /*
    val globalJob = GlobalScope.launch {
        repeat(COUNTDOWN_VAL) { i->
            println("Saket at ${COUNTDOWN_VAL - i} on ${Thread.currentThread().name}")
            delay(1000)
        }
    }
    println("Saket is going to cancel coroutine")
    Thread.sleep(3000)
    globalJob.cancel()
    println("Saket cancelled coroutine")
     */

    /*
    runBlocking {
        val job = launch {
            repeat(1000) {i ->
                println("Saket in job: sleeping $i")
                delay(500L)
            }
        }
        delay(1300)
        println("Saket in main: i am tierd fo waiting...")
        job.cancel()
        job.join()
        println("Saket in main: now i can quit.")
    }
     */

    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        repeat(10) { i ->
            println("Saket in $i")
            delay(1000)
        }
    }
    Thread.sleep(3000)
    scope.cancel()
}

/*
Here we see that it is possible to launch multiple
child coroutines from a coroutine scope. The coroutine
scope will wait for every child coroutine to finish
before closing.
 */
fun testLaunchCoroutineWithMultiplechildren() {
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        launch {
            repeat(10) { i ->
                println("Saket in coroutine 1 $i")
                delay(1000)
            }
        }
        launch {
            repeat(10) { i ->
                println("Saket in coroutine 2 $i")
                delay(2000)
            }
        }
        launch {
            repeat(20) { i ->
                println("Saket in coroutine 3 $i")
                delay(300)
            }
        }
    }

    /*
    if scope is cancelled then it also stops execution of
    all its child coroutines.
     */
    //Thread.sleep(3000)
    //scope.cancel()
}


fun countDown() {
    repeat(COUNTDOWN_VAL) { i ->
        println("Saket at ${COUNTDOWN_VAL - i} on ${Thread.currentThread().name}")
        Thread.sleep(1000)
    }
}

/*
One can use callback block to return value from a coroutine operation
like shown here...
 */
fun testDummyNetworkCallWithLaunch(response: (Int) -> Unit) {
    val myScope = CoroutineScope(Dispatchers.Default)
    myScope.launch {
        val result = doSomething()
        response.invoke(result)
    }
}

suspend fun doSomething() : Int {
    delay(3000)
    return 100
}