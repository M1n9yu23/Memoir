package com.bossmg.android.memoir.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bossmg.android.memoir.adapter.recycler.MemoAdapter
import com.bossmg.android.memoir.databinding.FragmentLatestBinding
import com.bossmg.android.memoir.ui.main.MainActivity.Companion.memos

private const val TAG = "LatestFragment"

class LatestFragment : Fragment() {

    private lateinit var memoAdapter: MemoAdapter
    private var binding: FragmentLatestBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLatestBinding.inflate(inflater, container, false)

        memoAdapter = MemoAdapter(memos)
        binding!!.latestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.latestRecyclerView.adapter = memoAdapter

        binding!!.latestRefresh.setOnRefreshListener {
            memoAdapter.updateMemos(memos)
            binding!!.latestRefresh.isRefreshing = false
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart...")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume...")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause...")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop...")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView...")
        binding = null
    }

}