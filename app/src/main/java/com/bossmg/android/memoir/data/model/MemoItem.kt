package com.bossmg.android.memoir.data.model

import android.graphics.Bitmap
import android.os.Build
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class MemoItem(
    val id: Int,
    val title: String,
    val description: String?,
    val date: String,
    val mood: String = "\uD83D\uDE0A 기쁨",
    val image: Bitmap?
) {
    val dateNumber: String
        get() = date.split("-")[2]

    val dayOfWeek: String
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26 이상: LocalDate 사용
                val dateParts = date.split("-").map { it.toInt() }
                val localDate = LocalDate.of(dateParts[0], dateParts[1], dateParts[2])
                localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
            } else {
                // API 26 미만: SimpleDateFormat 사용
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
                val dateObject = dateFormat.parse(date)
                val dayFormat = SimpleDateFormat("EEEE", Locale.KOREAN)
                dayFormat.format(dateObject)
            }
        }

    fun getImageByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}