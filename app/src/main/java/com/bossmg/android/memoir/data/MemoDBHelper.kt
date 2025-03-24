package com.bossmg.android.memoir.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.util.Log
import com.bossmg.android.memoir.MemoItem


private const val DATABASE_NAME = "memoDB"
private const val DATABASE_VERSION = 1
private const val TABLE_NAME = "MEMO_TB"
private const val COLUMN_ID = "_id"
private const val COLUMN_TITLE = "title"
private const val COLUMN_DESCRIPTION = "description"
private const val COLUMN_DATE = "date"
private const val COLUMN_IMAGE = "image"
private const val TAG = "MemoDBHelper"


class MemoDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_IMAGE BLOB
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        Log.d(TAG, "Upgrade Database $p1 -> $p2")
    }

    // 메모 추가
    fun insertMemo(memo: MemoItem) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, memo.title)
            put(COLUMN_DESCRIPTION, memo.description)
            put(COLUMN_DATE, memo.date)
            put(COLUMN_IMAGE, memo.getImageByteArray())
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // 모든 메모를 가져오는 함수
    fun getAllMemos(): List<MemoItem> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val memoList = mutableListOf<MemoItem>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            memoList.add(MemoItem(title, description, date, BitmapFactory.decodeByteArray(image, 0, image.size)))
        }

        cursor.close()
        db.close()
        return memoList
    }

    // 특정 날짜에 해당하는 메모를 가져오는 함수
    fun getMemosByDate(date: String): List<MemoItem> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?", arrayOf(date))
        val memoList = mutableListOf<MemoItem>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            memoList.add(MemoItem(title, description, date, BitmapFactory.decodeByteArray(image, 0, image.size)))
        }

        cursor.close()
        db.close()
        return memoList
    }
}