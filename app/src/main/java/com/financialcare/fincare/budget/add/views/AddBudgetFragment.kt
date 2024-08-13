package com.financialcare.fincare.budget.add.views

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
import com.example.fincare.databinding.FragmentAddBudgetBinding
import com.financialcare.fincare.budget.add.AddBudgetViewModel
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.dialog.LoadingDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder.show
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class AddBudgetFragment : BaseFragment<FragmentAddBudgetBinding>(R.layout.fragment_add_budget) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val context = requireContext()

        val month = YearMonth.of(args.year, args.month)

        addBudgetViewModel.setMonth(month)

        val formatter = DateTimeFormatter.ofPattern(context.getString(R.string.month_format))

        binding.info = context.getString(R.string.add_budget_info, month.format(formatter))

        addBudgetViewModel.savedBudget
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                findNavController().navigate(R.id.action_from_add_budget_to_budget)
            }
            .addTo(compositeDisposable)

        val notificationDialog = NotificationDialogBuilder.create(context, R.string.ok, DialogInterface::dismiss)

        addBudgetViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(
                    R.string.something_went_wrong,
                    R.string.something_went_wrong_message
                )
            }
            .addTo(compositeDisposable)

        val loadingDialog = LoadingDialogBuilder.create(context)

        addBudgetViewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) loadingDialog.show() else loadingDialog.dismiss()
            }
            .addTo(compositeDisposable)

        binding.isIncomeValid = addBudgetViewModel.isIncomeValid

        binding.setIncome = addBudgetViewModel::setIncome
        binding.add = addBudgetViewModel::add

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_from_add_budget_to_budget)
        }

        return binding.root
    }

    private val args: AddBudgetFragmentArgs by navArgs()

    private val addBudgetViewModel: AddBudgetViewModel by hiltNavGraphViewModels(
        R.id.add_budget_navigation
    )
}