package com.financialcare.fincare.main.views

import kotlin.system.exitProcess
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.fincare.R
import com.example.fincare.databinding.FragmentMainBinding
import com.financialcare.fincare.budget.one.views.BudgetFragment
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.page.PageAdapter
import com.financialcare.fincare.common.views.page.Screen
import com.financialcare.fincare.expenses.list.views.ExpensesFragment
import com.financialcare.fincare.reports.list.views.ReportsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
                exitProcess(0)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        adapter = PageAdapter(fragments, this)
        binding.mainViewPager.isUserInputEnabled = false
        binding.mainViewPager.adapter = adapter
        binding.mainViewPager.offscreenPageLimit = 2
        binding.selectNavigationItem = ::selectNavigationItem
        binding.switchPage = ::switchPage

        return binding.root
    }

    private fun selectNavigationItem(item: MenuItem): Boolean {
        val screen = fragments.findLast { it.menuItemId == item.itemId }
        return if (screen != null) {
            val pos = adapter.screens.indexOf(screen)
            if (pos != binding.mainViewPager.currentItem) {
                binding.mainViewPager.currentItem = pos
            }
            true
        } else {
            false
        }
    }

    private fun switchPage(pos: Int) {
        if (adapter.screens.size > pos) {
            val selected = adapter.screens[pos]
            binding.bnvBottomNavigation.selectedItemId = selected.menuItemId
        }
    }

    private val fragments = listOf(
        Screen(R.id.expenses_item, ExpensesFragment()),
        Screen(R.id.budget_item, BudgetFragment()),
        Screen(R.id.reports_item, ReportsFragment())
    )

    private lateinit var adapter: PageAdapter
}