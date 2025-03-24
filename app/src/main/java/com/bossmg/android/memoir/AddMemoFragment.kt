package com.bossmg.android.memoir

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.icu.util.Calendar
import android.net.Uri
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
import com.bossmg.android.memoir.databinding.FragmentAddMemoBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AddMemoFragment : Fragment() {

    private lateinit var binding: FragmentAddMemoBinding
    private var imageUri: Uri? = null
    private val pickCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            binding.memoImage.setImageBitmap(bitmap) // ImageView에 표시
        }
    }
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let{uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri) // URI -> Bitmap 변환
                binding.memoImage.setImageBitmap(bitmap)
            }
        }
    }
    private val pickContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { contactUri ->
                    val phoneNumber = getPhoneNumber(contactUri)
                    if (phoneNumber != null) {
                        shareMemo(phoneNumber)
                    } else {
                        Toast.makeText(requireContext(), "전화번호를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
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
            val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // 사용자가 선택한 날짜를 text_date에 설정
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                // 날짜 포맷을 지정하고 TextView에 표시
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.textDate.text = formattedDate
            }, year, month, day)

            datePickerDialog.show()
        }

        binding.memoAdd.setOnClickListener {
            val title = binding.title.text.toString()
            val description: String? = binding.description.text.toString()
            val date = binding.textDate.text.toString()
            val image: Bitmap? = (binding.memoImage.drawable as? BitmapDrawable)?.bitmap

            if(title.isNotBlank()) {
                val memoItem = MemoItem(title, description, date, image)
                MyApplication.db.insertMemo(memoItem)
                Toast.makeText(requireContext(), "저장되었습니다!", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }
        }

        binding.memoShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            pickContactLauncher.launch(intent)
        }

        binding.memoCamera.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("사진 선택")
                setIcon(android.R.drawable.ic_menu_gallery)
                setItems(arrayOf("카메라", "갤러리"), object: DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when(p1) {
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

    // 선택한 연락처에서 전화번호를 가져오는 함수
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
            data = Uri.parse("smsto:$phoneNumber") // 전화번호 설정
            putExtra("sms_body", memoText) // 메시지 내용 설정
        }
        startActivity(smsIntent)
    }

    private fun openCamera(){
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "memo_${System.currentTimeMillis()}.jpg")
        imageUri = FileProvider.getUriForFile(requireContext(), "com.bossmg.android.memoir.fileprovider", file)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri) // 사진을 저장할 위치 지정
        }
        pickCameraLauncher.launch(intent)
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickGalleryLauncher.launch(intent)
    }


}