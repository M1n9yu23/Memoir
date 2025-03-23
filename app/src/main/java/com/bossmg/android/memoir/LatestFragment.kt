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

    private lateinit var datas: List<MemoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        datas = List(10) {
            MemoItem("Title $it", "Description $it", "$it","2025-03-${it + 1}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLatestBinding.inflate(inflater, container, false)

        binding.latestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.latestRecyclerView.adapter = MemoAdapter(datas)

        return binding.root
    }

    private inner class MemoViewHolder(val binding: ItemMemoBinding): RecyclerView.ViewHolder(binding.root)

    private inner class MemoAdapter(private val memoList: List<MemoItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = MemoViewHolder(ItemMemoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

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