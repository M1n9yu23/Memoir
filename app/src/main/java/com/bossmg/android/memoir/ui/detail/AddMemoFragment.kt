package com.bossmg.android.memoir.ui.detail

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.bossmg.android.memoir.data.model.MemoItem
import com.bossmg.android.memoir.MyApplication
import com.bossmg.android.memoir.R
import com.bossmg.android.memoir.databinding.FragmentAddMemoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AddMemoFragment : Fragment() {

    private lateinit var binding: FragmentAddMemoBinding
    private var imageUri: Uri? = null // 카메라 Uri

    private val pickCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri!!)
                val bitmap = ImageDecoder.decodeBitmap(source)
                binding.memoImage.setImageBitmap(bitmap)
            } else {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    imageUri
                ) // URI -> Bitmap 변환
                binding.memoImage.setImageBitmap(bitmap)
            }
        }
    }
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { uri ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.memoImage.setImageBitmap(bitmap)
                } else {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        uri
                    ) // URI -> Bitmap 변환
                    binding.memoImage.setImageBitmap(bitmap)
                }
            }
        }
    }
    private val pickContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { contactUri ->
                    val phoneNumber = getPhoneNumber(contactUri)
                    if (phoneNumber != null) {
                        shareMemo(phoneNumber)
                    }
                }
            }
        }


    companion object {
        private const val ARG_MEMO_ID = "memoId"

        fun newInstance(memoId: Int?): AddMemoFragment {
            val fragment = AddMemoFragment()
            val args = Bundle()
            args.putInt(ARG_MEMO_ID, memoId ?: -1)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemoBinding.inflate(inflater, container, false)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        val formattedDate = dateFormat.format(calendar.time)

        binding.textDate.text = formattedDate

        binding.textDate.setOnClickListener {
            // DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // 사용자가 선택한 날짜를 text_date에 설정
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)

                    // 날짜 포맷을 지정하고 TextView에 표시
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.textDate.text = formattedDate
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }
/*

        binding.memoAdd.setOnClickListener {
            val title = binding.title.text.toString()
            val description: String? = binding.description.text.toString()
            val date = binding.textDate.text.toString()
            val mood = binding.moodSpinner.selectedItem.toString()
            val image: Bitmap? = (binding.memoImage.drawable as? BitmapDrawable)?.bitmap

            if (title.isNotBlank()) {
                val memoItem = MemoItem(0, title, description, date, mood, image)
                MyApplication.db.insertMemo(memoItem)
                Toast.makeText(requireContext(), "저장되었습니다!", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            } else {

            }
        }
*/

        binding.memoShare.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            pickContactLauncher.launch(intent)
        }

        binding.memoCamera.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("사진 선택")
                setIcon(android.R.drawable.ic_menu_gallery)
                setItems(arrayOf("카메라", "갤러리"), object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            0 -> openCamera()
                            1 -> openGallery()
                        }
                    }
                })
                setPositiveButton("닫기", null)
            }.show()
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moodArray = resources.getStringArray(R.array.mood_array)

        val memoId = arguments?.getInt(ARG_MEMO_ID, -1) ?: -1
        if (memoId != -1) {
            val memo = MyApplication.db.getMemo(memoId)
            memo?.let {
                binding.title.setText(it.title)
                binding.description.setText(it.description)
                binding.textDate.text = it.date
                binding.moodSpinner.setSelection(moodArray.indexOf(it.mood))
                it.image?.let { bitmap ->
                    binding.memoImage.setImageBitmap(bitmap)
                }
                binding.memoDelete.visibility = View.VISIBLE
            }
        } else {
            binding.memoDelete.visibility = View.GONE
        }

        binding.memoAdd.setOnClickListener {
            saveMemo(memoId)
        }

        binding.memoDelete.setOnClickListener {
            deleteMemo(memoId)
        }

    }

    // 저장을 담당하는 함수
    private fun saveMemo(memoId: Int) {
        val title = binding.title.text.toString()
        val description = binding.description.text.toString()
        val date = binding.textDate.text.toString()
        val mood = binding.moodSpinner.selectedItem.toString()
        val image: Bitmap? = (binding.memoImage.drawable as? BitmapDrawable)?.bitmap

        if (title.isNotBlank()) {
            val memoItem = MemoItem(memoId, title, description, date, mood, image)
            if (memoId == -1) {
                MyApplication.db.insertMemo(memoItem)
                Toast.makeText(requireContext(), "메모가 저장되었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                MyApplication.db.updateMemo(memoId, memoItem)
                Toast.makeText(requireContext(), "메모가 수정되었습니다!", Toast.LENGTH_SHORT).show()
            }
            requireActivity().finish()
        }
    }

    private fun deleteMemo(memoId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                MyApplication.db.deleteMemo(memoId)
                Toast.makeText(requireContext(), "메모가 삭제되었습니다!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 선택한 연락처에서 전화번호를 가져옴
    private fun getPhoneNumber(contactUri: Uri): String? {
        var phoneNumber: String? = null
        val cursor: Cursor? = requireActivity().contentResolver.query(
            contactUri,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                phoneNumber = it.getString(0)
            }
        }
        return phoneNumber
    }

    // SMS 공유 기능을 사용하여 메모 내용을 전송
    private fun shareMemo(phoneNumber: String) {
        val title = binding.title.text.toString()
        val description = binding.description.text.toString()
        val date = binding.textDate.text.toString()

        val memoText = "📅 날짜: $date\n📌 제목: $title\n📝 내용: $description"

        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms to:$phoneNumber") // 전화번호 설정
            putExtra("sms_body", memoText) // 메시지 내용 전달
        }
        startActivity(smsIntent)
    }

    // 카메라 열기
    private fun openCamera() {
        val file = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "memo_${System.currentTimeMillis()}.jpg"
        )
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.bossmg.android.memoir.fileprovider",
            file
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        pickCameraLauncher.launch(intent)
    }

    // 갤러리 열기
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickGalleryLauncher.launch(intent)
    }


}