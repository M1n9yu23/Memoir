package com.bossmg.android.memoir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.databinding.FragmentPhotoBinding
import com.bossmg.android.memoir.databinding.ItemPhotoBinding

private const val TAG = "PhotoFragment"

class PhotoFragment : Fragment() {

    private lateinit var memos: List<MemoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memos = MyApplication.db.getAllMemos()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPhotoBinding.inflate(inflater, container, false)

        binding.photoRecycler.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.photoRecycler.adapter = PhotoAdapter(memos)

        return binding.root
    }

    private inner class PhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    private inner class PhotoAdapter(private val memoList: List<MemoItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // 이미지 없는 데이터를 뺌
        private val filterMemoImages = memoList.filter { it.image != null}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        override fun getItemCount(): Int = filterMemoImages.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = (holder as PhotoViewHolder).binding
            val memo = filterMemoImages[position]
            binding.photo.setImageBitmap(memo.image)
        }
    }
}