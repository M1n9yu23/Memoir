package com.bossmg.android.memoir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.databinding.FragmentLatestBinding
import com.bossmg.android.memoir.databinding.ItemMemoBinding

class LatestFragment : Fragment() {

    private lateinit var memos: List<MemoItem>
    private lateinit var memoAdapter: MemoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memos = MyApplication.db.getAllMemos()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLatestBinding.inflate(inflater, container, false)

        memoAdapter = MemoAdapter(memos)
        binding.latestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.latestRecyclerView.adapter = memoAdapter

        return binding.root
    }

    private inner class MemoViewHolder(val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root)

    private inner class MemoAdapter(private var memoList: List<MemoItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            binding.memoDate.text = memo.date
            binding.memoDay.text = memo.dayOfWeek
            binding.memoMainImage.setImageBitmap(memo.image)
        }

        fun updateMemos(newMemoList: List<MemoItem>) {
            memoList = newMemoList
            notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        // db 갱신
        val updateMemos = MyApplication.db.getAllMemos()
        if (updateMemos != memos) {
            memos = updateMemos
            memoAdapter.updateMemos(memos)
        }
    }
}