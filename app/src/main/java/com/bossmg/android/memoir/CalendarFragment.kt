package com.bossmg.android.memoir

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bossmg.android.memoir.MainActivity.Companion.memos
import com.bossmg.android.memoir.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay


private const val TAG = "CalendarFragment"
class CalendarFragment : Fragment() {

    private val eventDates = HashSet<CalendarDay>()
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        memoAdapter = MemoAdapter(emptyList())
        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.calendarRecyclerView.adapter = memoAdapter

        for (memo in memos) {
            val dateParts = memo.date.split("-")
            val calendarDay = CalendarDay.from(dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
            eventDates.add(calendarDay)
        }

        // 캘린더뷰에 데코레이터 추가
        binding.calendarView.addDecorator(EventDecorator(Color.CYAN, eventDates))

        // 날짜 클릭 리스너 추가
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val year = date.year
            val month = date.month
            val day = date.day

            // "yyyy-MM-dd" 형식으로 날짜를 생성
            val formattedDate = "$year-${String.format("%02d", month)}-${String.format("%02d", day)}"

            val memos = MyApplication.db.getMemosByDate(formattedDate)
            memoAdapter.updateMemos(memos)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.db.getAllMemos() != memos) {
            for (memo in memos) {
                val dateParts = memo.date.split("-")
                val calendarDay = CalendarDay.from(dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
                eventDates.add(calendarDay)
            }
            binding.calendarView.invalidateDecorators()
        }
    }
}