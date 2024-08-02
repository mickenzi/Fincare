package com.financialcare.fincare.kinds.select.single

import androidx.lifecycle.ViewModel
import com.financialcare.fincare.kinds.Kind
import com.financialcare.fincare.kinds.KindsRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class SelectKindViewModel : ViewModel() {
    fun select(kind: String) {
        kindSubject.onNext(kind)
    }

    val kinds: Array<Kind> = KindsRepository.kinds.toTypedArray()

    val selectedKind: Observable<String>

    private val kindSubject = PublishSubject.create<String>()

    init {
        selectedKind = kindSubject
    }
}