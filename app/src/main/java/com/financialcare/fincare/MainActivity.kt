package com.financialcare.fincare

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import javax.inject.Inject
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fincare.R
import com.example.fincare.databinding.ActivityMainBinding
import com.financialcare.fincare.common.views.BaseActivity
import com.financialcare.fincare.expenses.ExpensesRepository
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main, R.id.main_nav_fragment) {
    @Inject
    lateinit var expensesRepository: ExpensesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val now = YearMonth.now()
        val time = OffsetDateTime.of(
            LocalDateTime.of(LocalDate.of(now.year, now.monthValue, 1), LocalTime.MIDNIGHT),
            ZoneOffset.UTC
        )

        expensesRepository.deleteAll(time)
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private val compositeDisposable = CompositeDisposable()
}