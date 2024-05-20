package com.saket.workingwithcoroutines.scopes

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/**
 * Coroutines create the following kind of hierarchy:
 *
 * Parent Coroutine
 * Child coroutine 1
 * Child coroutine 2
 * ...
 * Child coroutine N
 * Assume that "Coroutine i" fails. What do you want to happen with its parent?
 *
 * If you want for its parent to also fail, use coroutineScope. That's what structured concurrency is all about.
 *
 * But if you don't want it to fail, for example child was some kind of background task which can be started again, then use supervisorScope.
 */
class supervisorScope {

    suspend fun main() = println(compute())

    suspend fun compute(): String = coroutineScope {
        val color = async { delay(60_000); "purple" }
        val height = async<Double> { delay(100); throw Exception() }
        "A %s box %.1f inches tall".format(color.await(), height.await())
    }

    /*
    What behavior would you like for the above code?
    Would you like to color.await() for a minute, only
    to realize that the other network call has long failed?

    Or perhaps you'd like the compute() function to realize
    after 100 ms that one of its network calls has failed and
    immediately fail itself?

    With supervisorScope you're getting 1., with coroutineScope
    you're getting 2.
     */

    /*
    The behavior of 2. means that, even though async doesn't itself
    throw the exception (it just completes the Deferred you got from
    it), the failure immediately cancels its coroutine, which cancels
    the parent, which then cancels all the other children.

    Ref url:
    https://stackoverflow.com/questions/53577907/when-to-use-coroutinescope-vs-supervisorscope
     */
}