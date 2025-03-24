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
import com.bossmg.android.memoir.databinding.ItemMemoBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalendarFragment : Fragment() {

    private val eventDates = HashSet<CalendarDay>()
    private lateinit var memoAdapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val allMemos = MyApplication.db.getAllMemos()
        for (memo in allMemos) {
            val dateParts = memo.date.split("-")
            val calendarDay = CalendarDay.from(dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
            eventDates.add(calendarDay)
        }

        // 점 색상 설정
        val dotColor = Color.CYAN
        // 캘린더뷰에 데코레이터 추가
        binding.calendarView.addDecorator(EventDecorator(dotColor, eventDates))

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
            binding.memoDateNumber.text = memo.dateNumber
            binding.memoDate.text = "${memo.date} (${memo.dayOfWeek})"
            binding.memoDay.text = memo.dayOfWeek
            binding.memoMainImage.setImageBitmap(memo.image)
        }
    }
}