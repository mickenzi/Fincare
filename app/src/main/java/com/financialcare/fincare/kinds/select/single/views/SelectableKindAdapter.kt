package com.financialcare.fincare.kinds.select.single.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.fincare.R
import com.example.fincare.databinding.FragmentSelectableKindWithImageItemBinding
import com.financialcare.fincare.kinds.Kind

class SelectableKindAdapter(
    kinds: Array<Kind>,
    context: Context,
    private val select: (String) -> Unit
) : ArrayAdapter<Kind>(context, R.layout.fragment_selectable_kind_with_image_item, kinds) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let {
            DataBindingUtil.getBinding<FragmentSelectableKindWithImageItemBinding>(
                it
            )
        }
            ?: DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_selectable_kind_with_image_item,
                parent,
                false
            )

        val kind = getItem(position) as Kind

        binding.name = localize(kind.id)
        binding.iwKind.setImageResource(kind.image)
        binding.select = {
            select(kind.id)
        }

        return binding.root
    }

    private fun localize(id: String): String {
        val resId = context.resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resId)
    }
}