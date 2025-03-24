package com.bossmg.android.memoir

import android.app.Application
import com.bossmg.android.memoir.data.MemoDBHelper

class MyApplication: Application() {

    companion object{
        lateinit var db: MemoDBHelper
    }

    override fun onCreate() {
        super.onCreate()
        db = MemoDBHelper(applicationContext)
    }
}