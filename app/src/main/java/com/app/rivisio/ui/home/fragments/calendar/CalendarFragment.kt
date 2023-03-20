package com.app.rivisio.ui.home.fragments.calendar

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.app.rivisio.R
import com.app.rivisio.databinding.Example7CalendarDayBinding
import com.app.rivisio.databinding.FragmentCalendarBinding
import com.app.rivisio.utils.displayText
import com.app.rivisio.utils.getColorCompat
import com.app.rivisio.utils.getWeekPageTitle
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class CalendarFragment : Fragment() {

    private var selectedDate = LocalDate.now()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    private var _binding: FragmentCalendarBinding? = null

    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(requireView())
    }

    fun setUp(view: View) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = Example7CalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {

                    if (day.date > LocalDate.now()) {
                        //don't allow clicks on future dates
                        return@setOnClickListener
                    }

                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.exSevenCalendar.notifyDateChanged(day.date)
                        oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }
                        Timber.e("My log ................")
                    }

                    if (selectedDate == LocalDate.now()) {
                        //binding.calendarImage.setImageResource(R.drawable.daily_log_calendar)
                        //binding.calendarText.text = "Please log in your daily health log"
                        //calendarViewModel.getTodaysRecord()
                    }

                    if (selectedDate < LocalDate.now()) {
                        //binding.calendarImage.setImageResource(R.drawable.no_record)
                        //binding.calendarText.text = "No Data\n" + " Record Found !"
                        //calendarViewModel.getRecord(selectedDate)
                    }
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text =
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                if (day.date == selectedDate) {
                    bind.exSevenDateText.setTextColor(Color.WHITE)
                    bind.exSevenDayText.setTextColor(Color.WHITE)
                } else {
                    bind.exSevenDateText.setTextColor(Color.BLACK)
                    bind.exSevenDayText.setTextColor(Color.GRAY)
                }

                bind.blueEnclosure.isVisible = day.date == selectedDate

                bind.exSevenDayText.alpha = if (day.date <= LocalDate.now()) 1f else 0.3f
                bind.exSevenDateText.alpha = if (day.date <= LocalDate.now()) 1f else 0.3f

            }
        }

        binding.exSevenCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        binding.exSevenCalendar.weekScrollListener = { weekDays ->
            binding.tvDate.text = getWeekPageTitle(weekDays)
        }

        val currentMonth = YearMonth.now()
        binding.exSevenCalendar.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )

        binding.exSevenCalendar.scrollToDate(LocalDate.now())

        binding.btnRight.setOnClickListener {
            binding.exSevenCalendar.scrollToDate(
                binding.exSevenCalendar.findFirstVisibleDay()?.date?.plusDays(
                    7
                )!!
            )
        }

        binding.btnLeft.setOnClickListener {
            binding.exSevenCalendar.scrollToDate(
                binding.exSevenCalendar.findFirstVisibleDay()?.date?.minusDays(
                    7
                )!!
            )
        }

        /*calendarViewModel.isDatafetched.observe(this, Observer {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()

                    binding.calendarIllustrationContainer.visibility = View.GONE
                    binding.calenderDataContainer.visibility = View.VISIBLE

                    try {

                        binding.feltText.text =
                            it.data.asJsonObject[ApiConstants.FEELING_STR].asString
                        binding.dietText.text = it.data.asJsonObject[ApiConstants.DIET_STR].asString
                        binding.exerciseText.text =
                            it.data.asJsonObject[ApiConstants.EXERCISE_STR].asString
                        var medicineStr = ""

                        it.data.asJsonObject[ApiConstants.MEDICATIONS].asJsonArray.forEach { medications: JsonElement ->
                            medicineStr =
                                medicineStr.plus(medications.asJsonObject[ApiConstants.MEDICINE_NAME].asString + ",")
                        }
                        if (medicineStr.isEmpty())
                            binding.medicineText.text = "No Medicine Taken"
                        else
                            binding.medicineText.text = medicineStr
                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: $e")
                        showError("Something went wrong.")
                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    showError(it.exception.localizedMessage)
                    binding.calendarIllustrationContainer.visibility = View.VISIBLE
                    binding.calenderDataContainer.visibility = View.GONE

                }
            }
        })*/

        //calendarViewModel.getTodaysRecord()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}