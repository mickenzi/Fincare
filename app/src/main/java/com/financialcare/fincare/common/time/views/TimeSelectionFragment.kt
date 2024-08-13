package com.financialcare.fincare.common.time.views

import java.time.LocalTime
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.fincare.R
import com.example.fincare.databinding.FragmentTimeSelectionBinding

class TimeSelectionFragment(
    private val time: LocalTime,
    private val selectTime: (LocalTime) -> Unit,
    private val onDismiss: () -> Unit
) : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.time_selection_dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding: FragmentTimeSelectionBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_time_selection, container, false)

        binding.tpStart.hour = time.hour
        binding.tpStart.minute = time.minute

        binding.confirm = {
            val selectedTime = LocalTime.of(binding.tpStart.hour, binding.tpStart.minute)
            selectTime(selectedTime)
            dismiss()
        }

        binding.cancel = ::dismiss

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDismiss()
    }
}