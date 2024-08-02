package com.financialcare.fincare.common.views.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener<A>(
    private val layoutManager: LinearLayoutManager,
    private val viewAdapter: ListAdapter<A, *>
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (viewAdapter.currentList.isNotEmpty()) {
            val lastItem = viewAdapter.currentList.last()
            val lastVisibleItemPos = layoutManager.findLastCompletelyVisibleItemPosition()
            val itemCount = recyclerView.adapter?.itemCount ?: 0
            if (lastVisibleItemPos == itemCount - 1 && dy > 0) {
                onLoadMore(lastItem)
            }
        }
    }

    abstract fun onLoadMore(lastItem: A)
}