package com.saket.workingwithcoroutines.scopes

import android.view.View
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

//MainScope - uses Dispatchers.Main...so runs on the main thread by default.
//However it can be customized to run on the default threadpool thread as well...
fun testMainScope(view: View) {
    MainScope().launch {
        println("This is in the mainscope : ")
    }
}
