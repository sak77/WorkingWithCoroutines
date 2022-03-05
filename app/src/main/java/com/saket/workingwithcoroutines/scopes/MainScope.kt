package com.saket.workingwithcoroutines.scopes

import android.view.View
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * MainScope - Creates main CoroutineScope for UI components.
 *
 * The resulting scope has SupervisorJob and Dispatchers.Main
 * context elements...so runs on the main thread by default.
 *
 * If you want to append additional elements to the main scope,
 * use CoroutineScope plus operator:val scope = MainScope() +
 * CoroutineName("MyActivity")
 */
//
//However it can be customized to run on the default threadpool thread as well...
fun testMainScope(view: View) {
    MainScope().launch {
        println("This is in the mainscope : ")
    }
}
