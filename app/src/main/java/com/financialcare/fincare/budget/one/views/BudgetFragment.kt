package com.financialcare.fincare.budget.one.views

import java.time.YearMonth
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.fincare.R
import com.example.fincare.databinding.FragmentBudgetBinding
import com.financialcare.fincare.budget.Budget
import com.financialcare.fincare.budget.add.views.AddBudgetFragmentArgs
import com.financialcare.fincare.budget.edit.views.EditBudgetFragmentArgs
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

        val context = requireContext()

        if (childFragmentManager.findFragmentById(binding.flCalendar.id) == null) {
            val calendarFragment = CalendarFragment(
                selectMonth = budgetViewModel::selectMonth,
                selectedMonth = budgetViewModel.selectedMonth,
                minMonth = budgetViewModel.minMonth,
                maxMonth = budgetViewModel.maxMonth
            )
            childFragmentManager
                .beginTransaction()
                .add(binding.flCalendar.id, calendarFragment)
                .commit()
        }

        budgetViewModel.initBudget
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val args = AddBudgetFragmentArgs(it.year, it.monthValue)
                findNavController().navigate(R.id.action_from_budget_to_add_budget, args.toBundle())
            }
            .addTo(compositeDisposable)

        budgetViewModel.budget
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (budgetOpt, month) ->
                budgetOpt.fold(::bindEmptyBudget) { bindBudget(it, context) }

                val addBtnVisibility =
                    if (!budgetOpt.isPresent && month >= YearMonth.now()) View.VISIBLE else View.GONE
                binding.fabAddBudget.visibility = addBtnVisibility

                val editBtnVisibility = if (budgetOpt.isPresent && month >= YearMonth.now()) View.VISIBLE else View.GONE
                binding.fabEditBudget.visibility = editBtnVisibility
            }
            .addTo(compositeDisposable)

        val notificationDialog = NotificationDialogBuilder.create(context, R.string.ok, DialogInterface::dismiss)

        budgetViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(
                    R.string.something_went_wrong,
                    R.string.something_went_wrong_message
                )
            }
            .addTo(compositeDisposable)

        budgetViewModel.selectedBudget
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val args = EditBudgetFragmentArgs(it.year, it.monthValue)
                findNavController().navigate(R.id.action_from_budget_to_edit_budget, args.toBundle())
            }
            .addTo(compositeDisposable)

        binding.add = budgetViewModel::add

        binding.edit = budgetViewModel::edit

        return binding.root
    }

    private fun bindEmptyBudget() {
        binding.llBudget.visibility = View.GONE
        binding.tvEmptyBudget.visibility = View.VISIBLE
    }

    private fun bindBudget(budget: Budget, context: Context) {
        binding.tvEmptyBudget.visibility = View.GONE
        binding.llBudget.visibility = View.VISIBLE

        val currency = context.getString(R.string.currency)

        binding.income = "${budget.income} $currency"
        binding.debt = "${budget.debt} $currency"
        binding.balance = "${budget.balance} $currency"
        binding.maxExpenseAmount = "${budget.maxExpenseAmount} $currency"
        binding.maxExpenseKind = budget.maxExpenseKind?.let { localize(it, context) } ?: ""

        val maxExpenseAmountVisibility = budget.maxExpenseAmount?.let { View.VISIBLE } ?: View.GONE
        binding.tvMaxExpenseAmountTitle.visibility = maxExpenseAmountVisibility
        binding.tvMaxExpenseAmount.visibility = maxExpenseAmountVisibility

        val maxExpenseKindVisibility = budget.maxExpenseKind?.let { View.VISIBLE } ?: View.GONE
        binding.tvMaxExpenseKindTitle.visibility = maxExpenseKindVisibility
        binding.tvMaxExpenseKind.visibility = maxExpenseKindVisibility
    }

    private fun localize(id: String, context: Context): String {
        val resId = context.resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resId)
    }

    private val budgetViewModel: BudgetViewModel by hiltNavGraphViewModels(R.id.main_navigation)
}