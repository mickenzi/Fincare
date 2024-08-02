package com.financialcare.fincare.kinds.select.multi.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fincare.R
import com.example.fincare.databinding.FragmentSelectKindsBinding
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.kinds.Kind
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable

@AndroidEntryPoint
class SelectKindsFragment(
    private val kinds: Array<Kind>,
    private val selectedKinds: Observable<Set<String>>,
    private val selectKinds: (Set<String>) -> Unit
) : BaseFragment<FragmentSelectKindsBinding>(R.layout.fragment_select_kinds) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val adapter = SelectableKindsAdapter(
            kinds = kinds,
            selectedKinds = selectedKinds,
            context = requireContext(),
            compositeDisposable = compositeDisposable,
            selectKinds = selectKinds
        )
        binding.lvKinds.adapter = adapter

        return binding.root
    }
}