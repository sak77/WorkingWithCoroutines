package com.saket.workingwithcoroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 *
 */
class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*
        Fragment consists of 2 lifecycles -
        Fragment lifecycle and Fragment View lifecycle.
        While Fragment survives config changes, the
        fragment view is re-created on config change.
        Based on the requirement, we can execute coroutines
        tied to the respective lifecycle.
         */
        viewLifecycleOwner.lifecycleScope.launch {
            //Do something tied to Fragment View scope
        }

        lifecycleScope.launch {
            //Do something tied to fragment instance scope
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}