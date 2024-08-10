package com.financialcare.fincare.expenses.add.views

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.fincare.R
import com.example.fincare.databinding.FragmentAddExpenseBinding
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.dialog.LoadingDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder.show
import com.financialcare.fincare.expenses.add.AddExpenseViewModel
import com.financialcare.fincare.kinds.select.single.views.SelectKindFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class AddExpenseFragment : BaseFragment<FragmentAddExpenseBinding>(R.layout.fragment_add_expense) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setFragmentResultListener(SelectKindFragment.REQUEST_KEY) { _, args ->
            val kind = args.getString(SelectKindFragment.ARGS_KEY)

            kind?.let(addExpenseViewModel::selectKind)
        }

        val context = requireContext()

        addExpenseViewModel.kind
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.kind = localize(context, it)
            }
            .addTo(compositeDisposable)

        addExpenseViewModel.amount
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.amount = it
            }
            .addTo(compositeDisposable)

        addExpenseViewModel.createdExpense
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                findNavController().navigate(R.id.action_from_add_expense_to_expenses)
            }
            .addTo(compositeDisposable)

        val notificationDialog = NotificationDialogBuilder.create(
            context,
            R.string.ok,
            DialogInterface::dismiss
        )

        addExpenseViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(
                    R.string.something_went_wrong,
                    R.string.something_went_wrong_message
                )
            }
            .addTo(compositeDisposable)

        val loadingDialog = LoadingDialogBuilder.create(context)

        addExpenseViewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) loadingDialog.show() else loadingDialog.dismiss()
            }
            .addTo(compositeDisposable)

        binding.selectKind = {
            findNavController().navigate(R.id.action_from_add_expense_to_select_kind)
        }

        binding.setAmount = addExpenseViewModel::setAmount

        binding.isAmountValid = addExpenseViewModel.isAmountValid
        binding.areExpenseDataValid = addExpenseViewModel.areExpenseDataValid

        binding.create = addExpenseViewModel::create

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun localize(context: Context, id: String): String {
        val resId = context.resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resId)
    }

    private val addExpenseViewModel: AddExpenseViewModel by hiltNavGraphViewModels(
        R.id.add_expense_navigation
    )
}