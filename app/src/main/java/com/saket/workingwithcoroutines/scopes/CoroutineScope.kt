package com.saket.workingwithcoroutines.scopes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * CoroutineScope is an interface that contains instance
 * of CoroutineContext. It is a wrapper around CoroutineContext.
 *
 * Direct implementation of this interface is not recommended.
 * Instead one can call CoroutineScope() or MainScope() functions
 * to get its instance.
 *
 * It ties the life-time of the coroutine to the lifecycle of a
 * component like a view or UI controller (Activity, Fragment etc.)
 *
 * Using coroutine scope to launch or async a coroutine, will
 * return a job instance. Which can be used to start or cancel
 * the job in the coroutine.
 *
 * Coroutines are used to perform asynchronous tasks. It is also
 * possible to launch multiple coroutines within a scope.
 * Earlier, it was possible to launch coroutines without a scope.
 * But then this leads to problem where if a coroutine was launched
 * for a UI element and it takes too long to complete and the
 * user moves away from the UI element. Or worse, comes back and
 * tries to re-start the operation. Then there was no way for the
 * coroutine to be in sync with these actions. The coroutine was
 * not tied to the lifecycle of the UI element.
 *
 * To fix this, CoroutineScope was introduced. It provides a
 * way to tie the behavior of the coroutine with the lifecycle
 * of the scope. This can be done by calling start and cancel methods
 * from the returned job instance. Or by using LifecycleScope,
 * ViewmodelScope etc.
 *
 * Structured concurrency and CoroutineScope:
 * CoroutineScope waits for all coroutines inside the block
 * to complete before completing itself.
 *
 * If CoroutineScope is cancelled, then the child coroutines will
 * also get cancelled.
 */

/*
Android has first-party support for coroutine scope in all
entities with the lifecycle.
 */



/*
Apart from predefined scopes, one can also define custom scope.
 */
val customScope = CoroutineScope(Dispatchers.IO)
