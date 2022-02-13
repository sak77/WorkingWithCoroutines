package com.saket.workingwithcoroutines.scopes

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

/*
CoroutineScope tied to this LifecycleOwner's Lifecycle.
This scope will be cancelled when the Lifecycle is destroyed.
This scope is bound to Dispatchers.Main.immediate.
 */
fun lifeCycleScopeTrial(lifecycleOwner: LifecycleOwner) {
    //Executes the job when lifecycle enters start state..
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        println("Saket in lifecycle started")
    }
}
