package com.saket.workingwithcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val customScope = CoroutineScope(Dispatchers.IO)
    /**
     *
     * The purpose of this app is to take a closer look at coroutines.
     *
     * Focus on -
     * CoroutineScope and its sub-classes - GlobalScope, MainScope, CoroutineScope, ViewModelScope and LifeCycleScope.
     * Coroutine Context - main components - Job and Dispatcher
     * Coroutine builder(s) - launch, runBlocking, async-await
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //Can we switch scope of a coroutine after it has started?
    //Can we launch multiple coroutines in different scopes
    fun launchGlobalScopeWithNoParams(view : View) {
        //Using Global Scope is generally discouraged as in this case the coroutine
        //is alive throughout the lifetime of the app. It holds memory which may not be required and
        //hence it is inefficient.
        GlobalScope.launch {
            delay(3000) //wait for 3 seconds
            //Log.v(TAG, "Hello Coroutines!!")
            showMessage("Hello Coroutines!!")
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
            val result = async { calculateSum(3 , 4) }
            //delay(2000)
            Log.v(TAG, "Result of sum method: ${result.await()}")
            Log.v(TAG, "Continue with coroutine execution.")
        }
        Log.v(TAG, "Doing some work outside coroutine.")
    }


    fun calculateSum(a : Int, b : Int) : Int {
        Thread.sleep(3000)  //Introduce delay for 3 secs to mock some work
        return a + b
    }

    /*
    CoroutineScope: GlobalScope
    CoroutineBuilder: async/await
    Here we execute 3 async coroutines inside the global scope. Each coroutine operates
    using a different coroutineContext. And the final result is displayed in the logs.
     */
    fun asyncWithGlobalScopeAndContext(view: View) : Unit {
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
            Log.v(TAG, "Result of async operation : ADD: ${result1.await()} , Diff: ${result2.await()}, Product: ${result3.await()} ")
        }
    }

    fun calculateDiff(a : Int, b : Int) = a - b

    fun calculateDProduct(a : Int, b : Int) = a * b

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

    //MainScope - uses Dispatchers.Main...so runs on the main thread by default.
    //However it can be customized to run on the default threadpool thread as well...
    fun testMainScope(view: View) {
        MainScope().launch() {
            showMessage("This is in the mainscope : ")
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
}