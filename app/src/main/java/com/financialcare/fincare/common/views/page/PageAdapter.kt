package com.financialcare.fincare.common.views.page

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(
    val screens: List<Screen>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return screens[position].fragment
    }

    override fun getItemCount(): Int {
        return screens.size
    }
}