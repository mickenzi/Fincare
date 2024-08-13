package com.financialcare.fincare.expenses.filter.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.fincare.R
import com.example.fincare.databinding.FragmentExpensesFilterBinding
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.expenses.filter.FilterExpensesViewModel
import com.financialcare.fincare.expenses.list.ExpensesViewModel
import com.financialcare.fincare.kinds.select.multi.views.SelectKindsFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class ExpensesFilterFragment : BaseFragment<FragmentExpensesFilterBinding>(R.layout.fragment_expenses_filter) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        if (childFragmentManager.findFragmentById(binding.flKinds.id) == null) {
            childFragmentManager
                .beginTransaction()
                .add(
                    binding.flKinds.id,
                    SelectKindsFragment(
                        filterExpensesViewModel.kinds,
                        filterExpensesViewModel.selectedKinds,
                        filterExpensesViewModel::selectKinds
                    )
                )
                .commit()
        }

        expensesViewModel.selectedFilterParams
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filterExpensesViewModel.setFilterParams(it)
            }
            .addTo(compositeDisposable)

        filterExpensesViewModel.filterParams
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                expensesViewModel.filter(it)
                findNavController().popBackStack()
            }
            .addTo(compositeDisposable)

        binding.areFilterParamsSelected = filterExpensesViewModel.areFilterParamsSelected

        binding.apply = filterExpensesViewModel::apply
        binding.clear = filterExpensesViewModel::clear

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private val filterExpensesViewModel: FilterExpensesViewModel
        by hiltNavGraphViewModels(R.id.expenses_filter_navigation)
    private val expensesViewModel: ExpensesViewModel by hiltNavGraphViewModels(R.id.main_navigation)
}