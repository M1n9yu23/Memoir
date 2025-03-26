package com.bossmg.android.memoir.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.databinding.FragmentMoodBinding
import com.bossmg.android.memoir.databinding.ItemMoodBinding
import com.bossmg.android.memoir.ui.main.MainActivity.Companion.memos

private const val TAG = "MoodFragment"

class MoodFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMoodBinding.inflate(inflater, container, false)

        // 이모지 목록 가져오기
        val emojiList = getMoodEmojiList()

        // 이모지 중복 횟수 계산
        val moodCountMap = getMoodCount(memos, emojiList)
        val moodList = moodCountMap.map { (emoji, count) -> MoodItem(emoji, count) }

        binding.moodRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.moodRecycler.adapter = MoodAdapter(moodList)

        return binding.root
    }

    private inner class MoodViewHolder(val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root)

    private inner class MoodAdapter(private val moods: List<MoodItem>) :
        RecyclerView.Adapter<MoodViewHolder>() {

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
}