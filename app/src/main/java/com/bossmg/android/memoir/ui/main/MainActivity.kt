package com.bossmg.android.memoir.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bossmg.android.memoir.adapter.viewpager.FragmentViewPagerAdapter
import com.bossmg.android.memoir.adapter.recycler.MemoAdapter
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.MyApplication
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.databinding.ActivityMainBinding
import com.bossmg.android.memoir.ui.detail.MemoActivity
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), MemoAdapter.MemoItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val pagerAdapter = FragmentViewPagerAdapter(this)
        binding.viewpager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab1)
                1 -> getString(R.string.tab2)
                2 -> getString(R.string.tab3)
                3 -> getString(R.string.tab4)
                else -> ""
            }
        }.attach()

        binding.fab.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        var memos = MyApplication.db.getAllMemos()
        var updateMemos: List<MemoItem> = emptyList()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume...")
        updateMemos = MyApplication.db.getAllMemos()
    }

    override fun onMemoItemClick(memoId: Int) {
        val intent = Intent(this, MemoActivity::class.java)
        intent.putExtra("memoId", memoId)
        startActivity(intent)
    }
}