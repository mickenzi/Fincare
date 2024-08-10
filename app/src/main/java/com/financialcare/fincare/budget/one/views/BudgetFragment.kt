package com.financialcare.fincare.budget.one.views

import java.time.YearMonth
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.fincare.R
import com.example.fincare.databinding.FragmentBudgetBinding
import com.financialcare.fincare.budget.add.views.AddBudgetFragmentArgs
import com.financialcare.fincare.budget.one.BudgetViewModel
import com.financialcare.fincare.common.optional.fold
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder.show
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class BudgetFragment : BaseFragment<FragmentBudgetBinding>(R.layout.fragment_budget) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        budgetViewModel.budget
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it.fold(::redirectToAddBudget) {}
            }
            .addTo(compositeDisposable)

        val context = requireContext()

        val notificationDialog = NotificationDialogBuilder.create(
            context,
            R.string.ok,
            DialogInterface::dismiss
        )

        budgetViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(
                    R.string.something_went_wrong,
                    R.string.something_went_wrong_message
                )
            }
            .addTo(compositeDisposable)

        return binding.root
    }

    private fun redirectToAddBudget() {
        val now = YearMonth.now()
        val args = AddBudgetFragmentArgs(now.year, now.monthValue)
        findNavController().navigate(R.id.action_from_budget_to_add_budget, args.toBundle())
    }

    private val budgetViewModel: BudgetViewModel by hiltNavGraphViewModels(R.id.main_navigation)
}