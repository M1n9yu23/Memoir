package com.bossmg.android.memoir.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bossmg.android.memoir.ui.main.CalendarFragment
import com.bossmg.android.memoir.ui.main.LatestFragment
import com.bossmg.android.memoir.ui.main.MoodFragment
import com.bossmg.android.memoir.ui.main.PhotoFragment

class FragmentViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(LatestFragment(), CalendarFragment(), MoodFragment(), PhotoFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}