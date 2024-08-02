package com.financialcare.fincare.kinds

import com.example.fincare.R

object KindsRepository {
    val kinds: List<Kind> =
        listOf(
            //First class
            Kind("food_and_drink", R.drawable.ic_baseline_food_and_drink_24),
            Kind("utilities", R.drawable.ic_baseline_utilities_24),
            Kind("grocery", R.drawable.ic_baseline_grocery_24),
            Kind("transport", R.drawable.ic_baseline_transport_24),
            Kind("pet", R.drawable.ic_baseline_pet_24),
            //Second class
            Kind("entertainment", R.drawable.ic_baseline_entertainment_24),
            Kind("gym", R.drawable.ic_baseline_gym_24),
            Kind("clothing", R.drawable.ic_baseline_clothing_24),
            Kind("healthcare", R.drawable.ic_baseline_healthcare_24),
            Kind("beauty", R.drawable.ic_baseline_beauty_24),
            //Others
            Kind("debt_payments", R.drawable.ic_baseline_debt_24),
            Kind("education", R.drawable.ic_baseline_education_24),
            Kind("investments", R.drawable.ic_baseline_investments_24),
            Kind("other", R.drawable.ic_baseline_other_24)
        )
}