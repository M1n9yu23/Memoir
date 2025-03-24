package com.bossmg.android.memoir

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bossmg.android.memoir.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}