package com.financialcare.fincare.common.views.bindings

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.fragment.app.findFragment
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.bindings.TextInputLayoutExtension.setInvalidStyle
import com.financialcare.fincare.common.views.bindings.TextInputLayoutExtension.setValidStyle
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo

object ObservableBindingAdapters {
    @JvmStatic
    @BindingAdapter("rxVisible")
    fun rxVisible(view: View, visible: Observable<Boolean>) {
        val baseFragment = view.findFragment<BaseFragment<Nothing>>()

        visible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isVisible ->
                view.visibility = if (isVisible) View.VISIBLE else View.GONE
            }, {
                view.visibility = View.GONE
            })
            .addTo(baseFragment.compositeDisposable)
    }

    @JvmStatic
    @BindingAdapter(value = ["rxValidation", "error"], requireAll = false)
    fun rxValidation(textInput: TextInputLayout, observable: Observable<Boolean>, errorText: String) {
        val baseFragment = textInput.findFragment<BaseFragment<Nothing>>()

        observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isValid ->
                if (isValid) {
                    textInput.setValidStyle()
                } else {
                    textInput.setInvalidStyle(errorText)
                }
            }
            .addTo(baseFragment.compositeDisposable)
    }

    @JvmStatic
    @BindingAdapter("rxEnable")
    fun rxEnable(view: View, observable: Observable<Boolean>) {
        val baseFragment = view.findFragment<BaseFragment<Nothing>>()

        observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.isEnabled = it
            }
            .addTo(baseFragment.compositeDisposable)
    }
}