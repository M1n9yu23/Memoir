package com.bossmg.android.memoir.decorator

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(private val color: Int, private val eventDates: HashSet<CalendarDay>) :
    DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return eventDates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(8f, color))
    }
}