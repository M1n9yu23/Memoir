package com.bossmg.android.memoir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bossmg.android.memoir.databinding.FragmentCalendarBinding
import com.bossmg.android.memoir.databinding.FragmentLatestBinding

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }
}