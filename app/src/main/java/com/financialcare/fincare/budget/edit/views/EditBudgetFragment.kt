package com.financialcare.fincare.budget.edit.views

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fincare.R
import com.example.fincare.databinding.FragmentEditBudgetBinding
import com.financialcare.fincare.budget.edit.EditBudgetViewModel
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder.show
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class EditBudgetFragment : BaseFragment<FragmentEditBudgetBinding>(R.layout.fragment_edit_budget) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        editBudgetViewModel.setMonth(YearMonth.of(args.year, args.month))

        val context = requireContext()

        editBudgetViewModel.budget
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val formatter = DateTimeFormatter.ofPattern(context.getString(R.string.month_format))

                binding.income = it.income.toString()
                binding.info = context.getString(R.string.edit_budget_info, it.month.format(formatter))
            }
            .addTo(compositeDisposable)

        editBudgetViewModel.savedBudget
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                findNavController().popBackStack()
            }
            .addTo(compositeDisposable)

        val notificationDialog = NotificationDialogBuilder.create(context, R.string.ok, DialogInterface::dismiss)

        editBudgetViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(R.string.something_went_wrong, R.string.something_went_wrong_message)
            }
            .addTo(compositeDisposable)

        binding.isIncomeValid = editBudgetViewModel.isIncomeValid

        binding.setIncome = editBudgetViewModel::setIncome
        binding.edit = editBudgetViewModel::save

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private val args: EditBudgetFragmentArgs by navArgs()

    private val editBudgetViewModel: EditBudgetViewModel by hiltNavGraphViewModels(R.id.edit_budget_navigation)
}