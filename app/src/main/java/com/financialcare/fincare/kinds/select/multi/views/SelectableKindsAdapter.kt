package com.financialcare.fincare.kinds.select.multi.views

import java.util.Collections
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.fincare.R
import com.example.fincare.databinding.FragmentSelectableKindItemBinding
import com.financialcare.fincare.kinds.Kind
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

class SelectableKindsAdapter(
    kinds: Array<Kind>,
    selectedKinds: Observable<Set<String>>,
    context: Context,
    compositeDisposable: CompositeDisposable,
    private val selectKinds: (Set<String>) -> Unit
) : ArrayAdapter<Kind>(context, R.layout.fragment_selectable_kind_item, kinds) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let {
            DataBindingUtil.getBinding<FragmentSelectableKindItemBinding>(
                it
            )
        }
            ?: DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_selectable_kind_item,
                parent,
                false
            )

        val kind = getItem(position) as Kind
        binding.kind = localize(kind.id)

        binding.checkKind.setOnCheckedChangeListener { _, _ -> }
        binding.checkKind.isChecked = checkedKinds.contains(kind.id)
        binding.checkKind.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkedKinds.add(kind.id) else checkedKinds.remove(kind.id)

            selectKinds(checkedKinds)
        }

        return binding.root
    }

    private fun localize(id: String): String {
        val resId = context.resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resId)
    }

    private val checkedKinds: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())

    init {
        selectedKinds
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkedKinds.addAll(it)
                notifyDataSetChanged()
            }
            .addTo(compositeDisposable)

        selectedKinds
            .skip(1)
            .filter(Set<String>::isEmpty)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkedKinds.clear()
                notifyDataSetChanged()
            }
            .addTo(compositeDisposable)
    }
}