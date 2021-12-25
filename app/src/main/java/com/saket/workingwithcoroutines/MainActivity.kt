package com.saket.workingwithcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

/**
 *
 * The purpose of this app is to take a closer look at coroutines.
 *
 * Focus on CoroutineScope and its sub-classes -
 * ViewModelScope
 * LifeCycleScope
 * GlobalScope
 * MainScope
 * CoroutineScope
 * Coroutine Context - main components - Job and Dispatcher
 * Coroutine builder(s) - launch, runBlocking, async-await
 *
 */
class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    /*
    Apart from predefined scopes, one can also define custom scope.
     */
    val customScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        btnStart.setOnClickListener { view ->
            run {
                MainScope().launch {
                    //setTextAfterDelay("Hello Saket!")
                    lazyAsyncCoroutine()
                }
            }
        }
         */
    }

    /*
    Suspend functions will suspend execution of the calling coroutine/function
    while they execute.
    Suspend can only be called by other suspend functions or Coroutines.
    Coroutines can call suspend as well regular functions.
    Suspended functions can call public suspended functions like:
    coroutineScope, delay and withContext.
    As a rule of thumb, use regular function with coroutines unless the compiler
    asks you to use 'suspend' keyword.
     */
    suspend fun setTextAfterDelay(sayHello: String) {
        /*
        Delay is a suspend function. So it can only be called from another suspend function.
        So add suspend modifier to the calling function
         */
        delay(2000)
        textView.setText(sayHello)
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

    /*
    CoroutineScope - GlobalScope
    CoroutineBuilder - launch
    By default the CouroutineBuilder inherits context from CoroutineScope.
    However, we can also provide our own parameters to the CoroutineBuilder class.
    With CoroutineContext we can specify the dispatcher and job for the coroutine.
     */
    fun launchGlobalScopeWithParams(view: View) {
        //You launch in GlobalScope with some additional params -
        GlobalScope.launch(context = Dispatchers.Main, block = {
            delay(3000)
            showMessage("Hello Again!")
        }, start = CoroutineStart.DEFAULT)
    }

    /*
    CoroutineScope: GlobalScope
    CoroutineBuilder - async.
    async returns the un-determined result of the operation as a deferred object.
    The value of the deferred object will be set once the coroutine completes its task.
    This value can be obtained using await() function.
     */
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
    CoroutineScope: GlobalScope
    CoroutineBuilder: async/await
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

    //CoroutineScope with launch
    /*
    CoroutineContext allows us to specify in greater detail how the coroutine executes.
    With CoroutineScope we have to provide the CoroutineContext.
    CoroutineScope allows us to control the lifecycle of the coroutine.
     */
    fun coroutineScopeWithLaunch(view: View) {
        btnPause.visibility = View.VISIBLE  //Update view visibility
        customScope.launch {
            showMessage("CoroutineScope with Dispatchers.Main and launch")
            for (i in 0..1000) {
                showMessage("$i ")
                delay(500)
            }
        }
    }

    //CoroutineScope with async-await
    /*
    Notice how i am using the coroutineScope instance to launch 2 separate Coroutines.
    First i use async to calculate the Sum. Then i launch another Coroutine to display
    the result. It is observed that both coroutines execute on separate threads from the
    default thread-pool....
    Btw i have not used the cancel here as it was not necessary..
     */
    fun asyncWithCoroutineScope(view: View) {
        val customScope2 = CoroutineScope(Dispatchers.IO)
        val result = customScope2.async {
            delay(1000)
            showMessage("Calculating sum in async")
            calculateSum(22, 34)
        }

        customScope2.launch {
            showMessage("Sum is ${result.await()}")
        }
    }

    /*
    viewModelScope is part of the ViewModel and it gets cancelled when the viewModel is cleared.
    You must add KTX dependencies for viewModelScope and lifecycleScope to use them..
     */
    fun viewModelScopeTrial(view: View) {
        val myViewModel: MyViewModel by viewModels()
        myViewModel.launchCoroutine()
    }

    /*
    LifecycleScope executes the coroutine within the current lifecycle scope.
     */
    fun lifeCycleScopeTrial() {
        //Executes the job when lifecycle enters start state..
        lifecycleScope.launchWhenStarted {

        }
    }

    //runBlocking by default runs with coroutine scope of the current thread's event loop.
    //however we can provide our own coroutineContext which provides a coroutineDispatcher.
    //when coroutineDispatcher is specified, the coroutine runs in the scope of the dispatcher.
    //SHOULD NOT be used from a coroutine. It is used to used in 'main' functions and
    //to run tests...
    fun runBlockingWithoutParams(view: View) {
        runBlocking {
            showMessage("Calling delay in coroutine...")
            delay(4000)
            showMessage("Continue work in coroutine....")
        }
        showMessage("Doing work on the main thread....")
    }

    //MainScope - uses Dispatchers.Main...so runs on the main thread by default.
    //However it can be customized to run on the default threadpool thread as well...
    fun testMainScope(view: View) {
        MainScope().launch() {
            showMessage("This is in the mainscope : ")
        }
    }

    //run blocking with coroutineContext
    fun runBlockingWithCoroutineContext(view: View) {
        //Using Dispatchers.Main with runBlocking causes the app to crash for some reason??
        runBlocking(Dispatchers.Default) {
            showMessage("Calling delay in coroutine...")
            delay(4000)
            showMessage("Continue work in coroutine....")
        }
        showMessage("Doing work on the main thread....")
    }

    fun cancelCoroutineScope(view: View) {
        btnPause.visibility = View.INVISIBLE  //Update view visibility
        if (customScope.isActive) {
            showMessage("Going to sleep on MainThread for 2 secs...")
            //When customJob uses Dispatchers.Main then the below Thread.Sleep causes it to also
            //pause for 2 secs. However if i use Dispatchers.IO then below Thread.sleep only pauses
            //Main thread, but the coroutine continues to execute uninterrupted.
            Thread.sleep(2000)
            showMessage("Cancel customJob")
            customScope.cancel()
        }
    }

    //This is just a trial...should not be used..
    fun testRunBlockingInsideCoroutine(view: View) {
        //runBlocking should NOT be called from a coroutine.
        //As seen below, it gives a warning - inappropriate blocking method call.
        //Since it tries to block the default dispatcher thread maybe or is not effective in
        //blocking the main thread as it is intended....
        CoroutineScope(Dispatchers.Main).launch {
            runBlocking {
                showMessage("Running runBlocking inside CoroutineScope.launch")
            }
        }
    }

}