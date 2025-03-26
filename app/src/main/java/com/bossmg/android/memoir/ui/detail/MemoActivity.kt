package com.bossmg.android.memoir.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.databinding.ActivityMemoBinding

class MemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMemoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val memoId = intent.getIntExtra("memoId", -1)

        if(savedInstanceState == null) {
            val fragment = AddMemoFragment.newInstance(memoId)
            supportFragmentManager.beginTransaction()
                .add(R.id.memo_activity_container, fragment)
                .commit()
        }
    }
}