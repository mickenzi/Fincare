package com.financialcare.fincare.common.views.bindings

import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("onPageChange")
    fun onPageChange(view: ViewPager2, callback: (Int) -> Unit) {
        view.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                callback(position)
            }
        })
    }

    @JvmStatic
    @BindingAdapter("onNavigationItemSelected")
    fun onNavigationItemSelected(view: BottomNavigationView, callback: (MenuItem) -> Boolean) {
        view.setOnItemSelectedListener(callback)
    }

    @JvmStatic
    @BindingAdapter("onTextChange")
    fun onTextChange(editText: EditText, callback: (String) -> Unit) {
        editText.addTextChangedListener(object : AfterTextChangedWatcher {
            override fun afterTextChanged(editable: Editable) {
                callback(editable.toString())
            }
        })
    }

    @JvmStatic
    @BindingAdapter("onFocus")
    fun onFocus(view: View, callback: (Boolean) -> Unit) {
        view.setOnFocusChangeListener { _, hasFocus ->
            callback(hasFocus)
        }
    }

    private interface AfterTextChangedWatcher : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        }
    }
}