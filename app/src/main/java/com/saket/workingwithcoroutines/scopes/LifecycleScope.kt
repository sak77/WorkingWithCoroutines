package com.saket.workingwithcoroutines.scopes

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/*
CoroutineScope tied to a LifecycleOwner's Lifecycle.
This scope will be cancelled when the Lifecycle is destroyed.
This scope is bound to Dispatchers.Main.immediate.
 */

/*
Lifecycle provides additional methods: lifecycle.whenCreated, lifecycle.whenStarted, and lifecycle.whenResumed.
Any coroutine run inside these blocks is suspended if the Lifecycle isn't at least in the minimal desired state.

Below coroutine is launched whenever lifecycle reaches the
RESUMED state. The Coroutine is cancelled once lifecycle
moves to another state..when user closes the app or rotates the screen.
 */
fun launchCoroutineWhenLifecycleResumed(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycleScope.launchWhenResumed {
        try {
            repeat(10) {
                delay(1000)
                println("Saket coroutine doing some work")
            }
        } finally {
            println("Saket coroutine cancelled.")
        }
    }
}
/*
Note: Even though these methods provide convenience when working with Lifecycle,
you should use them only when the information is valid within the scope of the Lifecycle
(precomputed text, for example). Keep in mind that if the activity restarts, the coroutine
is not restarted.

Warning: Prefer collecting flows using the repeatOnLifecycle API instead of collecting inside the launchWhenX APIs.
As the latter APIs suspend the coroutine instead of cancelling it when the Lifecycle is STOPPED,
upstream flows are kept active in the background, potentially emitting new items and wasting resources.
 */

/*
Lifecycle and LifecycleOwner provide APIs to start and cancel coroutine collection
at certain lifecycle events.
 */
fun repeatOnLifeCycleStart(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycleScope.launch {
        // repeatOnLifecycle launches the block in a new coroutine every time the
        // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow<String> {
                try {
                    repeat(10) {
                        delay(500)
                        println("Saket in flow")
                    }
                }finally {
                    println("Saket flow cancelled.")
                }
            }.collect()
        }
    }
}

/*
If you only need to perform lifecycle-aware collection on a single flow,
you can use the Flow.flowWithLifecycle() method to simplify your code
 */
fun lifecycleAwareFlowCollection(lifecycleOwner: LifecycleOwner) {
    flow {
        try {
            repeat(10) {
                delay(500)
                emit("Saket in flow")
            }
        }finally {
            println("Saket flow cancelled..")
        }
    }
        //Using a shorthand way of collecting flows..for more details refer KotlinFlows project.
        .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
        .onEach { value -> println(value) }
        .launchIn(CoroutineScope(Dispatchers.Default))
    /*
    However, if you need to perform lifecycle-aware collection on multiple flows in parallel,
    then you must collect each flow in different coroutines.
    In that case, it's more efficient to use repeatOnLifecycle() directly
     */
}

