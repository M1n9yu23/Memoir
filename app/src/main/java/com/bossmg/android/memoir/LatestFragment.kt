package com.bossmg.android.memoir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bossmg.android.memoir.MainActivity.Companion.memos
import com.bossmg.android.memoir.MainActivity.Companion.updateMemos
import com.bossmg.android.memoir.databinding.FragmentLatestBinding

private const val TAG = "LatestFragment"

class LatestFragment : Fragment() {

    private lateinit var memoAdapter: MemoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLatestBinding.inflate(inflater, container, false)

        memoAdapter = MemoAdapter(memos)
        binding.latestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.latestRecyclerView.adapter = memoAdapter

        binding.latestRefresh.setOnRefreshListener {
            memoAdapter.updateMemos(updateMemos)
            binding.latestRefresh.isRefreshing = false
        }
        
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }
}