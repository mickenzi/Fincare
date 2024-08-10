package com.financialcare.fincare.expenses.list.views

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.fincare.R
import com.example.fincare.databinding.FragmentExpenseHeaderBinding
import com.example.fincare.databinding.FragmentExpenseItemBinding
import com.financialcare.fincare.common.identifiable.Identifiable
import com.financialcare.fincare.expenses.Expense
import com.financialcare.fincare.kinds.KindsRepository

class ExpensesAdapter(
    private val context: Context,
    private val delete: (Expense) -> Unit
) : ListAdapter<ExpensesAdapter.Companion.ExpenseItem, RecyclerView.ViewHolder>(itemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewBinding = DataBindingUtil.inflate(inflater, viewType, parent, false)
        return when (viewType) {
            R.layout.fragment_expense_header -> ExpenseHeaderViewHolder(
                binding as FragmentExpenseHeaderBinding
            )
            R.layout.fragment_expense_item -> ExpenseItemViewHolder(
                binding as FragmentExpenseItemBinding
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExpenseItemViewHolder -> {
                val expense = currentList[position] as ExpenseItem.Item

                holder.binding.kind = localize(expense.kind)
                holder.binding.time = expense.time.format(timeFormatter)
                holder.binding.amount = context.getString(R.string.amount, expense.amount)
                holder.binding.iwKind.setImageResource(
                    expense.image ?: R.drawable.ic_baseline_expense_24
                )
            }

            is ExpenseHeaderViewHolder -> {
                val date = currentList[position] as ExpenseItem.Header
                holder.binding.date = date.id
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is ExpenseItem.Header -> R.layout.fragment_expense_header
            is ExpenseItem.Item -> R.layout.fragment_expense_item
        }
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].id.hashCode().toLong()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun submit(expenses: List<Expense>) {
        val groupedExpenses = expenses.groupBy { it.time.toLocalDate() }
        val ds = groupedExpenses.keys.sortedWith { first, second ->
            when {
                first == second -> 0
                first == null -> -1
                second == null -> 1
                else -> -first.compareTo(second)
            }
        }

        val expensesGroups = ds.flatMap { date ->
            val header = ExpenseItem.Header(date.format(dateFormatter))
            val items = groupedExpenses[date]?.map(ExpenseItem::Item) ?: emptyList()
            listOf(header) + items
        }

        submitList(expensesGroups)
    }

    private fun localize(id: String): String {
        val resId = context.resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resId)
    }

    private val screenSpace = context.resources.getDimension(R.dimen.screen_spacing).toInt()
    private val dateFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.date_format))
    private val timeFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.time_format))

    class ExpenseItemViewHolder(val binding: FragmentExpenseItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
    class ExpenseHeaderViewHolder(val binding: FragmentExpenseHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    private inner class ItemTouchHelperCallback : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT
    ) {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return if (viewHolder is ExpenseItemViewHolder) {
                super.getMovementFlags(
                    recyclerView,
                    viewHolder
                )
            } else {
                0
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            if (viewHolder is ExpenseItemViewHolder) {
                val alpha = 1 - abs(dX / 700)
                viewHolder.binding.clExpense.alpha = if (isCurrentlyActive) alpha else 1.0f

                val background = createBackground(viewHolder.itemView)
                val deleteIcon = createDeleteIcon(viewHolder.itemView)

                if (isCurrentlyActive) {
                    background.draw(c)
                    deleteIcon?.draw(c)
                }
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return viewHolder is ExpenseItemViewHolder && target is ExpenseItemViewHolder
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (viewHolder is ExpenseItemViewHolder) {
                val position = viewHolder.adapterPosition
                val item = getItem(position) as ExpenseItem.Item

                delete(Expense(item.id, item.time, item.amount, item.kind))

                notifyItemChanged(position)
            }
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return SWIPE_THRESHOLD
        }

        private fun createBackground(itemView: View): Drawable {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.red))

            val backgroundLeft = itemView.width + screenSpace
            val backgroundTop = itemView.top
            val backgroundBottom = itemView.bottom

            background.setBounds(backgroundLeft, backgroundTop, 0, backgroundBottom)

            return background
        }

        private fun createDeleteIcon(itemView: View): Drawable? {
            val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)

            val iconHeight = deleteIcon?.intrinsicHeight ?: 0
            val iconWidth = deleteIcon?.intrinsicWidth ?: 0

            val iconTop = itemView.top + (itemView.height - iconHeight) / 2
            val iconBottom = iconTop + iconHeight
            val iconLeft = itemView.right - screenSpace - iconWidth
            val iconRight = itemView.right - screenSpace

            deleteIcon?.setTint(ContextCompat.getColor(context, R.color.white))
            deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            return deleteIcon
        }
    }

    companion object {
        private val kinds = KindsRepository.kinds

        sealed class ExpenseItem : Identifiable {
            data class Header(
                override val id: String
            ) : ExpenseItem()

            data class Item(
                override val id: String,
                val time: OffsetDateTime,
                val amount: Long,
                val kind: String,
                @DrawableRes val image: Int?
            ) : ExpenseItem() {
                constructor(expense: Expense) : this(
                    id = expense.id,
                    time = expense.time,
                    amount = expense.amount,
                    kind = expense.kind,
                    image = kinds.findLast { it.id == expense.kind }?.image
                )
            }
        }

        private fun itemDiffCallback(): DiffUtil.ItemCallback<ExpenseItem> {
            return object : DiffUtil.ItemCallback<ExpenseItem>() {
                override fun areItemsTheSame(oldItem: ExpenseItem, newItem: ExpenseItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: ExpenseItem, newItem: ExpenseItem): Boolean {
                    return oldItem == newItem
                }
            }
        }

        private const val SWIPE_THRESHOLD = 0.5f
    }
}