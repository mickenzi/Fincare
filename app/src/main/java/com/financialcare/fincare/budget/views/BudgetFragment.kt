package com.financialcare.fincare.budget.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fincare.R
import com.example.fincare.databinding.FragmentBudgetBinding
import com.financialcare.fincare.common.views.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BudgetFragment : BaseFragment<FragmentBudgetBinding>(R.layout.fragment_budget) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }
}