package com.bossmg.android.memoir.adapter.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.databinding.ItemMemoBinding
import java.text.SimpleDateFormat
import java.util.Locale

private val memoMood = "\uD83D\uDCDD 메모"
private val positiveMoods =
    setOf("\uD83D\uDE0A 기쁨", "\uD83E\uDD70 행복", "\uD83E\uDD29 설렘", "\uD83D\uDE0E 뿌듯함")
private val neutralMoods = setOf("\uD83D\uDE10 무난함", "\uD83E\uDD14 생각에 잠김")
private val negativeMoods = setOf(
    "\uD83D\uDE22 슬픔",
    "\uD83D\uDE21 화남",
    "\uD83D\uDE30 불안함",
    "\uD83D\uDE1E 실망함",
    "\uD83D\uDE29 피곤함"
)

class MemoAdapter(private var memoList: List<MemoItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var memoItemClickListener: MemoItemClickListener? = null

    interface MemoItemClickListener {
        fun onMemoItemClick(memoId: Int)
    }

    init {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val sortedList = memoList.sortedBy { memo ->
            sdf.parse(memo.date)
        }

        memoList = sortedList
    }

    private inner class MemoViewHolder(val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MemoViewHolder(
            ItemMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val binding = (holder as MemoViewHolder).binding
        val memo = memoList[position]

        memoItemClickListener = binding.root.context as MemoItemClickListener?

        binding.memoTitle.text = memo.title
        binding.memoDateNumber.text = memo.dateNumber
        binding.memoDate.text = "${memo.date} (${memo.dayOfWeek})"
        binding.memoDay.text = memo.dayOfWeek
        binding.moodText.text = memo.mood
        binding.memoMainImage.setImageBitmap(memo.image)

        binding.itemCardview.setOnClickListener {
            memoItemClickListener?.onMemoItemClick(memo.id)
        }

        when (memo.mood) {
            in positiveMoods -> {
                binding.itemCardview.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_positive_bg)
                )
                binding.itemCardview.setStrokeColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_positive_stroke)
                )
            }
            in neutralMoods -> {
                binding.itemCardview.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_neutral_bg)
                )
                binding.itemCardview.setStrokeColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_neutral_stroke)
                )
            }
            in negativeMoods -> {
                binding.itemCardview.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_negative_bg)
                )
                binding.itemCardview.setStrokeColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_negative_stroke)
                )
            }
            memoMood -> {
                binding.itemCardview.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_memo_bg)
                )
                binding.itemCardview.setStrokeColor(
                    ContextCompat.getColor(binding.root.context, R.color.mood_memo_stroke)
                )
            }
        }
    }

    fun updateMemos(newMemoList: List<MemoItem>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        memoList = newMemoList.sortedBy { memo ->
            sdf.parse(memo.date)
        }
        notifyDataSetChanged() // 데이터가 변경되었음을 알리고 뷰를 새로 고침
    }

}
