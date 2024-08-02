package com.financialcare.fincare.common.views.page

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

class Screen(
    @IdRes val menuItemId: Int,
    val fragment: Fragment
)