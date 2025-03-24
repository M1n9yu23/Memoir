package com.bossmg.android.memoir

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.databinding.FragmentCalendarBinding
import com.bossmg.android.memoir.databinding.FragmentLatestBinding
import com.bossmg.android.memoir.databinding.ItemMemoBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalendarFragment : Fragment() {

    private lateinit var datas: List<MemoItem>
    private val eventDates = HashSet<CalendarDay>()
    private lateinit var memoAdapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        datas = List(10) {
            MemoItem("Title $it", "Description $it", "$it", "2025-03-${it + 1}")
        }

        for (memo in datas) {
            val dateParts = memo.date.split("-")  // "2024-03-23" → [2024, 03, 23]
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt()
            val day = dateParts[2].toInt()
            eventDates.add(CalendarDay.from(year, month, day))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCalendarBinding.inflate(inflater, container, false)

        memoAdapter = MemoAdapter(emptyList())

        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.calendarRecyclerView.adapter = memoAdapter

        // 점 색상 설정 (예: 빨간색)
        val dotColor = Color.RED
        // 캘린더뷰에 데코레이터 추가
        binding.calendarView.addDecorator(EventDecorator(dotColor, eventDates))

        // 날짜 클릭 리스너 추가
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val selectedDate = date
            val filteredMemos = datas.filter {
                // MemoItem의 date를 CalendarDay로 변환하여 비교
                val memoDateParts = it.date.split("-")
                val memoCalendarDay = CalendarDay.from(
                    memoDateParts[0].toInt(),
                    memoDateParts[1].toInt(),
                    memoDateParts[2].toInt()
                )
                memoCalendarDay == selectedDate // 날짜 비교
            }
            memoAdapter.updateMemos(filteredMemos) // RecyclerView 데이터 업데이트
        }

        return binding.root
    }

    private inner class MemoViewHolder(val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root)

    private inner class MemoAdapter(private var memoList: List<MemoItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        fun updateMemos(newMemoList: List<MemoItem>) {
            memoList = newMemoList
            notifyDataSetChanged() // 데이터가 변경되었음을 알리고 뷰를 새로 고침
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            MemoViewHolder(
                ItemMemoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        override fun getItemCount(): Int = memoList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = (holder as MemoViewHolder).binding

            val memo = memoList[position]

            binding.memoTitle.text = memo.title
            binding.memoDateNumber.text = memo.dateDateNumber
            binding.memoDate.text = memo.date
        }
    }
}