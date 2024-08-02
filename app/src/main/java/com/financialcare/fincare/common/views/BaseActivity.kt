package com.financialcare.fincare.common.views

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment

open class BaseActivity<A : ViewDataBinding>(
    private val layoutId: Int,
    private val navFragmentId: Int
) : AppCompatActivity(), NavController.OnDestinationChangedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutId)
        getNavController(navFragmentId).addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        currentFocus?.hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        currentFocus?.hideKeyboard()
    }

    private fun getNavController(fragmentId: Int): NavController {
        val navFragment = supportFragmentManager.findFragmentById(fragmentId) as NavHostFragment
        return navFragment.navController
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private var _binding: A? = null
    private val binding get() = _binding!!
}