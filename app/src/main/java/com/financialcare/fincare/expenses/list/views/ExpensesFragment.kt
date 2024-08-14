package com.financialcare.fincare.expenses.list.views

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.example.fincare.R
import com.example.fincare.databinding.FragmentExpensesBinding
import com.financialcare.fincare.budget.one.BudgetViewModel
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.dialog.DeleteDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder.show
import com.financialcare.fincare.common.views.recyclerview.EndlessRecyclerViewScrollListener
import com.financialcare.fincare.expenses.list.ExpensesViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class ExpensesFragment : BaseFragment<FragmentExpensesBinding>(R.layout.fragment_expenses) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val context = requireContext()

        val expensesAdapter = ExpensesAdapter(context, expensesViewModel::delete)
        expensesAdapter.setHasStableIds(true)

        val expenseLayoutManager = LinearLayoutManager(context)

        binding.rvExpenses.apply {
            setHasFixedSize(true)
            layoutManager = expenseLayoutManager
            adapter = expensesAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == SCROLL_STATE_IDLE) {
                        binding.fabAddExpense.show()
                    } else {
                        binding.fabAddExpense.hide()
                    }
                }
            })
        }

        expensesViewModel.expenses
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                expensesAdapter.submit(it)
            }
            .addTo(compositeDisposable)

        expensesViewModel.deletedExpense
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                budgetViewModel.reload()
            }
            .addTo(compositeDisposable)

        val notificationDialog = NotificationDialogBuilder.create(
            context,
            R.string.ok,
            DialogInterface::dismiss
        )

        expensesViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(
                    R.string.something_went_wrong,
                    R.string.something_went_wrong_message
                )
            }
            .addTo(compositeDisposable)

        expensesViewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (!it) binding.cpExpenses.visibility = View.GONE
            }
            .addTo(compositeDisposable)

        expensesViewModel.isRefreshing
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.refreshExpenses.isRefreshing = it
            }
            .addTo(compositeDisposable)

        val deleteDialog = DeleteDialogBuilder.create(
            context,
            R.string.delete_expense,
            R.string.delete_expense_confirm,
            expensesViewModel::confirmDelete,
            expensesViewModel::rejectDelete
        )

        expensesViewModel.startDeleting
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                deleteDialog.show()
            }
            .addTo(compositeDisposable)

        expensesViewModel.areExpensesEmpty
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.emptyPlaceholder.llPlaceholder.visibility = if (it) View.VISIBLE else View.GONE
            }
            .addTo(compositeDisposable)

        val scrollListener = object : EndlessRecyclerViewScrollListener<ExpensesAdapter.Companion.ExpenseItem>(
            expenseLayoutManager,
            expensesAdapter
        ) {
            override fun onLoadMore(lastItem: ExpensesAdapter.Companion.ExpenseItem) {
                if (lastItem is ExpensesAdapter.Companion.ExpenseItem.Item) {
                    expensesViewModel.loadMore(lastItem.id, lastItem.time)
                }
            }
        }

        binding.rvExpenses.addOnScrollListener(scrollListener)

        binding.refreshExpenses.setOnRefreshListener {
            expensesViewModel.reload()
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.filter) {
                findNavController().navigate(R.id.action_from_expenses_to_expenses_filter)
                true
            } else {
                false
            }
        }

        binding.add = {
            expensesViewModel.reset()
            findNavController().navigate(R.id.action_from_expenses_to_add_expense)
        }

        return binding.root
    }

    private val expensesViewModel: ExpensesViewModel by hiltNavGraphViewModels(R.id.main_navigation)
    private val budgetViewModel: BudgetViewModel by hiltNavGraphViewModels(R.id.main_navigation)
}