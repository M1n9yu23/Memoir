package com.bossmg.android.memoir.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.adapter.recycler.MemoAdapter
import com.bossmg.android.memoir.databinding.FragmentMoodBinding
import com.bossmg.android.memoir.databinding.ItemMoodBinding
import com.bossmg.android.memoir.ui.main.MainActivity.Companion.memos

private const val TAG = "MoodFragment"

class MoodFragment : Fragment() {

    private lateinit var moodAdapter: MoodAdapter
    private lateinit var memoAdapter: MemoAdapter
    private var binding: FragmentMoodBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoodBinding.inflate(inflater, container, false)!!

        moodAdapter = MoodAdapter(changeMemo(memos))
        memoAdapter = MemoAdapter(emptyList())

        binding!!.moodRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding!!.moodRecycler.adapter = moodAdapter
        binding!!.memoMoodRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding!!.memoMoodRecycler.adapter = memoAdapter

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moodAdapter.onMoodClickListener = { emoji ->
            val filterMoodMemos = memos.filter { it.mood.startsWith(emoji)} // emoji로 시작하는 것을 필터
            memoAdapter.updateMemos(filterMoodMemos)
        }
    }

    private inner class MoodViewHolder(val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root)

    private inner class MoodAdapter(private var moods: List<MoodItem>) :
        RecyclerView.Adapter<MoodViewHolder>() {

            var onMoodClickListener: ((String) -> Unit)? = null

        fun updateMoods(newMoods: List<MoodItem>) {
            moods = newMoods
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder =
            MoodViewHolder(
                ItemMoodBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        override fun getItemCount(): Int = moods.size

        override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
            val binding = holder.binding
            val mood = moods[position]
            binding.moodEmoji.text = mood.emoji
            binding.moodNumber.text = mood.count.toString()

            binding.moodEmoji.setOnClickListener { view ->
                onMoodClickListener?.let {
                    it(mood.emoji)
                } // 수신자 객체가 람다로 전달되면서 위에 정의한 함수를 실행
            }

        }
    }

    private data class MoodItem(val emoji: String, val count: Int)

    // 이모지만 추출하여 리스트로 반환
    private fun getMoodEmojiList(): List<String> {
        val moodArray = resources.getStringArray(R.array.mood_array)
        return moodArray.map { it.split(" ")[0] } // 이모지 부분만 추출
    }

    // 메모에서 이모지 중복 개수 구하기
    private fun getMoodCount(memos: List<MemoItem>, emojiList: List<String>): Map<String, Int> {
        val moodCountMap = mutableMapOf<String, Int>()

        // 메모에서 이모지만 추출하여 카운트
        for (memo in memos) {
            val emoji = memo.mood.split(" ")[0]
            if (emoji in emojiList) {
                moodCountMap[emoji] = moodCountMap.getOrDefault(emoji, 0) + 1
            }
        }

        return moodCountMap
    }

    private fun changeMemo(memoList: List<MemoItem>): List<MoodItem> {
        // 이모지 목록 가져오기
        val emojiList = getMoodEmojiList()

        // 이모지 중복 횟수 계산
        val moodCountMap = getMoodCount(memoList, emojiList)
        val moodList = moodCountMap.map { (emoji, count) -> MoodItem(emoji, count) }

        return moodList
    }

    override fun onStart() {
        super.onStart()
        moodAdapter.updateMoods(changeMemo(memos))
        Log.d(TAG, "onStart...")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView...")
        binding = null
    }
}