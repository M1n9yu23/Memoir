package com.bossmg.android.memoir.ui.main

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bossmg.android.memoir.adapter.viewpager.FragmentViewPagerAdapter
import com.bossmg.android.memoir.adapter.recycler.MemoAdapter
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.MyApplication
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.databinding.ActivityMainBinding
import com.bossmg.android.memoir.notification.NotificationScheduler
import com.bossmg.android.memoir.ui.detail.MemoActivity
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), MemoAdapter.MemoItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Android 12 (API 31) 이상에서 알람 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // 알람 권한을 요청하도록 유도
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            } else {
                // 권한이 이미 허용된 경우 알람 설정
                scheduleNotifications()
            }
        } else {
            scheduleNotifications()
        }

        val pagerAdapter = FragmentViewPagerAdapter(this)
        binding.viewpager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.icon = when (position) {
                0 -> AppCompatResources.getDrawable(this, R.drawable.baseline_format_list_bulleted_24)
                1 -> AppCompatResources.getDrawable(this, R.drawable.baseline_calendar_month_24)
                2 -> AppCompatResources.getDrawable(this, R.drawable.baseline_emoji_emotions_24)
                3 -> AppCompatResources.getDrawable(this, R.drawable.baseline_image_24)
                else -> null
            }
        }.attach()

        binding.fab.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        var memos = MyApplication.db.getAllMemos()
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

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart...")
        memos = MyApplication.db.getAllMemos()
    }

    override fun onMemoItemClick(memoId: Int) {
        val intent = Intent(this, MemoActivity::class.java)
        intent.putExtra("memoId", memoId)
        startActivity(intent)
    }

    private fun scheduleNotifications() {
        NotificationScheduler.scheduleNightNotification(this)
        NotificationScheduler.scheduleMorningNotification(this)
    }
}