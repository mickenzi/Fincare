package com.financialcare.fincare.common.views.placeholder

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.content.res.getResourceIdOrThrow
import androidx.databinding.DataBindingUtil
import com.example.fincare.R
import com.example.fincare.databinding.FragmentContentPlaceholderBinding

class ContentPlaceholderView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        val inflater = LayoutInflater.from(context)
        val binding: FragmentContentPlaceholderBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_content_placeholder, this, true)

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.ContentPlaceholderView, 0, 0)

        val layout = attributes.getResourceIdOrThrow(R.styleable.ContentPlaceholderView_layout)
        val numOfRepetitions = attributes.getInteger(R.styleable.ContentPlaceholderView_numberOfRepetitions, 1)

        if (numOfRepetitions > MAX_NUMBER_OF_REPETITIONS) throw IllegalArgumentException()

        (1..numOfRepetitions).forEach { _ ->
            val placeholderItem = inflater.inflate(layout, null)
            placeholderItem.animation = AnimationUtils.loadAnimation(context, R.anim.glow)
            binding.llPlaceholder.addView(placeholderItem)
        }
    }

    private companion object {
        private const val MAX_NUMBER_OF_REPETITIONS = 30
    }
}

