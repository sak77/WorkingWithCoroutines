package com.saket.workingwithcoroutines

import kotlinx.coroutines.*
import java.lang.IndexOutOfBoundsException
import java.lang.UnsupportedOperationException

/*
By default, the exceptions thrown inside a coroutine started with launch don't require
to be handled and are printed instead to the console. They are treated as uncaught
exceptions. However, they still can be handled by using a CoroutineExceptionHandler.
 */
fun launch_exceptions() {
    CoroutineScope(Dispatchers.Default).launch {
        val job1 = launch {
            //Exceptions thrown inside launch are not propagated outwards....
            //if not handled, they are treated as uncaught exception
            try {
                delay(3000)
                println("Saket Throwing exception")
                throw IndexOutOfBoundsException()
            } catch (ex: java.lang.Exception) {
                println("Saket Handling exception inside launch")
            }
        }
        job1.join()
        println("Saket joined failed job..")
    }
}

/*
One can provide a CoroutineExceptionHandler to handle throwables.
CoroutineExceptionHandler is invoked if the Coroutine is launched in a separate context.
 */
fun launch_coroutine_with_coroutineexceptionhandler() {
    runBlocking {
        val myHandler = CoroutineExceptionHandler {
                _, exception ->
            println("Saket $exception handled.")
        }
        val job = CoroutineScope(Dispatchers.Main).launch (myHandler) {
            delay(3000)
            println("Saket throwing exception")
            throw UnsupportedOperationException()
        }
        //job.join does not execute the uncaught exception??
        //job.join()
    }
}


fun async_throws_exception_to_await() {
    runBlocking {
        val deferredResult = CoroutineScope(Dispatchers.Default).async {
            delay(3000)
            throw IndexOutOfBoundsException()
        }

        println("Saket Doing some other work...")
        delay(4000)
        try {
            deferredResult.await()
            println("Unreached")
        } catch (ex: Exception) {
            println("Saket caught await exception ${ex.localizedMessage}")
        }
    }

}