package com.bossmg.android.memoir.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.databinding.FragmentPhotoBinding
import com.bossmg.android.memoir.databinding.ItemPhotoBinding
import com.bossmg.android.memoir.ui.main.MainActivity.Companion.memos
import com.bossmg.android.memoir.ui.main.MainActivity.Companion.updateMemos

private const val TAG = "PhotoFragment"

class PhotoFragment : Fragment() {

    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var filteredMemoList: List<MemoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPhotoBinding.inflate(inflater, container, false)

        filteredMemoList = memos.filter { it.image != null }

        photoAdapter = PhotoAdapter(filteredMemoList)

        binding.photoRecycler.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.photoRecycler.adapter = photoAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private inner class PhotoViewHolder(val binding: ItemPhotoBinding)
        : RecyclerView.ViewHolder(binding.root)

    private inner class PhotoAdapter(private var memoList: List<MemoItem>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        override fun getItemCount(): Int = memoList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = (holder as PhotoViewHolder).binding
            val memo = memoList[position]
            binding.photo.setImageBitmap(memo.image)
        }

        fun updateMemos(newMemoList: List<MemoItem>) {
            memoList = newMemoList.filter { it.image != null }
            notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart...")
    }

    override fun onResume() {
        super.onResume()
        if(updateMemos != memos) {
            photoAdapter.updateMemos(updateMemos)
        }
    }
}