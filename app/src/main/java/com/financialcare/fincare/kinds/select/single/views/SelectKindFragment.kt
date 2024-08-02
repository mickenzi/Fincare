package com.financialcare.fincare.kinds.select.single.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fincare.R
import com.example.fincare.databinding.FragmentSelectKindBinding
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.kinds.select.single.SelectKindViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class SelectKindFragment : BaseFragment<FragmentSelectKindBinding>(R.layout.fragment_select_kind) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val adapter = SelectableKindAdapter(
            kinds = selectKindViewModel.kinds,
            context = requireContext(),
            select = selectKindViewModel::select
        )

        binding.lvKinds.adapter = adapter

        selectKindViewModel.selectedKind
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setFragmentResult(REQUEST_KEY, bundleOf(ARGS_KEY to it))
                findNavController().popBackStack()
            }
            .addTo(compositeDisposable)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    companion object {
        const val REQUEST_KEY = "selectKind"
        const val ARGS_KEY = "kind"
    }

    private val selectKindViewModel: SelectKindViewModel by viewModels()
}