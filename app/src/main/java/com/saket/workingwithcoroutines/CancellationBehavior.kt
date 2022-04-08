package com.saket.workingwithcoroutines

import kotlinx.coroutines.*
import java.io.IOException

/**
 * Cancelling a coroutine/scope will cancel its child coroutines.
 * But if the coroutine is doing computational work, then
 * cancellation becomes co-operative. Which means, the
 * child coroutine(s) are required to check if the
 * coroutine is still active before they proceed with next
 * iteration. Otherwise, calling job.cancel or scope.cancel
 * will not cancel the child coroutines until they complete
 * their operations.
 */

fun cancelRegularCoroutineJob() {
    runBlocking {
        val job = launch {
            repeat(1000) {i ->
                println("Saket in job: sleeping $i")
                delay(1500L)
            }
        }
        delay(1300)
        println("Saket in main: i am tierd fo waiting...")
        job.cancel()    //Cancels the job
        job.join()  //Suspends the coroutine until this job is complete.
        println("Saket in main: now i can quit.")
    }

    /*
    //One may also cancel the scope, which will cancel the jobs running.
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        repeat(10) { i ->
            println("Saket in $i")
            delay(1000)
        }
    }
    Thread.sleep(3000)
    scope.cancel()
     */
}


fun cancelComputationCoroutine() {
    val startTime = System.currentTimeMillis()
    val testScope = CoroutineScope(Dispatchers.Default)
    testScope.launch {
        val job = launch {
            var nextPrintTime = startTime
            var i = 0
            /*
            If a coroutine is working in a computation and does not
            check for cancellation, then it cannot be cancelled.
             */
            /*
            while (i < 10) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
             */
            /*
            Instead one must check for isActive in the
            computation to make cancellation cooperative.
             */
            while (isActive) {
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }
}

/*
When a coroutine is cancelled using Job.cancel, it terminates,
but it does not cancel its parent.
 */
fun cancellingJobDoesNotCancelItsParent() {
    CoroutineScope(Dispatchers.Default).launch {
        val child = launch {
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Saket Child is cancelled")
            }
        }
        yield()
        println("Saket Cancelling child")
        child.cancel()
        child.join()
        yield()
        println("Saket parent is still not cancelled.")
    }
}

/*
Canceling a child coroutine does not impact its siblings or parent.
If all child coroutines are cancelled, but the parent will still finish
its work before completing.
 */
fun testCancelSiblings() {
    runBlocking {
        val parent = CoroutineScope(Dispatchers.Default).launch {
            val child1 = launch {
                repeat(10) {
                    delay(200)
                    println("Saket child 1 doing work...")
                }
            }
            delay(1000)
            println("Saket cancel child 1")
            child1.cancel() //Cancel throws a CancellationException

            val child2 = launch {
                repeat(10) {
                    delay(500)
                    println("Saket child 2 doing work...")
                }
            }
            delay(1500)
            println("Saket cancel child 2")
            child2.cancel() //Cancel throws a CancellationException

            repeat(20) {
                delay(1000)
                println("Saket parent doing some work...")
            }
        }
        parent.join()
        println("Saket Parent completed doing work")
    }
}


fun originalExceptionIsHandledByParentWhenAllChildrenTerminate() {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Saket CoroutineExceptionHandler got $exception")
    }

    runBlocking {
        val job = GlobalScope.launch(handler) {
            launch { // the first child
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    withContext(NonCancellable) {
                        println("Saket Children are cancelled, but exception is not handled until all children terminate")
                        delay(100)
                        println("Saket The first child finished its non cancellable block")
                    }
                }
            }
            launch { // the second child
                delay(100)
                println("Saket Second child throws an exception")
                throw ArithmeticException()
                /*
                a child throwing an exception other than cancellationException
                will probably cancel its siblings..
                unless they run within a non-cancellable context..
                 */
            }
        }
        job.join()
    }
}

/*
When multiple children of a coroutine fail with an exception,
the general rule is "the first exception wins", so the first exception gets handled.
All additional exceptions that happen after
the first one are attached to the first exception as suppressed ones.
 */
fun firstExceptionWins() {
    runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Saket CoroutineExceptionHandler got $exception with suppressed ${exception.suppressed.contentToString()}")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE) // it gets cancelled when another sibling fails with IOException
                } finally {
                    throw ArithmeticException() // the second exception
                }
            }
            launch {
                delay(100)
                throw IOException() // the first exception
            }
            delay(Long.MAX_VALUE)
        }
        job.join()
    }
}

/*
Cancellation is bi-directional?? .i.e. it propogates through hiearchy of coroutines.
But for unidirectional cancellation flow- where only the parent cancel
affects child and not the other way round, use a supervisor job: SupervisorJob

For more details refer-
https://kotlinlang.org/docs/exception-handling.html#supervision

 */
