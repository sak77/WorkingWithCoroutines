package com.saket.workingwithcoroutines.scopes

import androidx.activity.ComponentActivity
import androidx.activity.viewModels

/*
viewModelScope is part of the ViewModel and it gets cancelled when the viewModel is cleared.
You must add KTX dependencies for viewModelScope and lifecycleScope to use them..
 */
fun viewModelScopeTrial(componentActivity: ComponentActivity) {
    with(componentActivity) {
        val myViewModel: MyViewModel by viewModels()
        myViewModel.launchCoroutine()
    }
}
