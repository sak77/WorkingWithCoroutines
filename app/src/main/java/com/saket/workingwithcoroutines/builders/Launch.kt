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

/*
Here we see that it is possible to launch multiple
child coroutines from a coroutine scope. The coroutine
scope will wait for every child coroutine to finish
before closing.
 */
fun testLaunchCoroutineWithMultipleChildren() {
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
    val myJob = myScope.launch {
        val result = doSomething()
        response.invoke(result)
    }
    /*
    Passing callback function to coroutines may cause memory leaks. If the function holds some
    reference to context of Android component. Instead another way to invoke code after
    coroutine execution is to return Job instance and call invokeOnCompletion.
     */
    myJob.invokeOnCompletion {
        //DO SOMETHING
    }

}

suspend fun doSomething() : Int {
    delay(3000)
    return 100
}