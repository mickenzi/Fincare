package com.financialcare.fincare.reports.list.views

import java.util.Locale
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.fincare.R
import com.example.fincare.databinding.FragmentReportsBinding
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder
import com.financialcare.fincare.common.views.dialog.NotificationDialogBuilder.show
import com.financialcare.fincare.reports.Budget
import com.financialcare.fincare.reports.list.ReportsViewModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentReportsBinding>(R.layout.fragment_reports) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        reportsViewModel.year
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                reportsViewModel.setYear(it)
                binding.currentYear = it.toString()
            }
            .addTo(compositeDisposable)

        val context = requireContext()

        reportsViewModel.reports
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val income = it.map(Budget::income)
                val balance = it.map(Budget::balance)
                val debt = it.map(Budget::debt)

                val xAsis = context.getString(R.string.x_asis)
                val yAsis = context.getString(R.string.y_asis)

                val months = context.getString(R.string.months)
                val price = context.getString(R.string.price_with_currency)

                binding.xAsis = "$xAsis - $months"
                binding.yAsis = "$yAsis - $price"

                binding.balanceChartView.setModel(
                    CartesianChartModel(
                        LineCartesianLayerModel.build {
                            series(x = reportsViewModel.months, y = income)
                            series(x = reportsViewModel.months, y = balance)
                            series(x = reportsViewModel.months, y = debt)
                        }
                    )
                )
            }.addTo(compositeDisposable)

        val notificationDialog = NotificationDialogBuilder.create(context, R.string.ok, DialogInterface::dismiss)

        reportsViewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notificationDialog.show(R.string.something_went_wrong, R.string.something_went_wrong_message)
            }
            .addTo(compositeDisposable)

        reportsViewModel.kinds
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { kinds ->
                binding.tvEmptyMaxKind.visibility = if (kinds.isEmpty()) View.VISIBLE else View.GONE

                val kindsView = binding.llKinds.children.map { it as TextView }.toList()
                kindsView.forEachIndexed { index, textView ->
                    val kind = kinds.getOrNull(index)

                    val localizedKind = kind?.let { localize(it.first, context) }
                    val percent = kind?.let { String.format(Locale.getDefault(), "%.1f", kind.second) }
                    val maxKind = kind?.let { "$localizedKind $percent%" } ?: ""

                    textView.text = maxKind
                }
            }.addTo(compositeDisposable)

        binding.increaseYear = reportsViewModel::increaseYear
        binding.decreaseYear = reportsViewModel::decreaseYear

        return binding.root
    }

    private fun localize(id: String, context: Context): String {
        val resId = context.resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resId)
    }

    private val reportsViewModel: ReportsViewModel by hiltNavGraphViewModels(R.id.main_navigation)
}