package com.example.vs

import FoodLabelAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.vs.databinding.ActivityResultBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var filePath: String
    private val foodRepository = FoodRepository()

    // 1. UI 미리보기를 위한 가짜 음식 라벨 데이터
    private val mockFoodLabels = listOf("밥", "시금치", "소고기 뭇국", "김치", "밥")

//    // 1. Retrofit API 서비스 객체 생성 (지연 초기화)
//    private val apiService: ApiService by lazy {
//        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
//        val client = OkHttpClient.Builder().addInterceptor(logging).build()
//        Retrofit.Builder()
//            .baseUrl("http://172.30.1.90:8000") // 👈 나중에 실제 서버 주소로 변경
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }

    private val requestCameraFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.resultImageView.load(File(filePath))
            // 2. API 호출 대신 가짜 데이터로 목록을 즉시 표시
            setupRecyclerViewWithMockData()
        }
    }


//    // 1. 카메라 촬영 결과를 처리하기 위한 런처
//    private val requestCameraFileLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        // 사진 촬영을 성공적으로 다시 마쳤다면
//        if (result.resultCode == RESULT_OK) {
//            // 현재 화면의 이미지를 새로 찍은 사진으로 교체
//            binding.resultImageView.load(File(filePath))
//            //>>>>>>api 호출부 집어 넣어야할곳<<<<<<<<<<<<
//        }
//    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityResultBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // 처음 MainActivity로부터 받은 사진 파일 경로
//        val imagePath = intent.getStringExtra("imagePath")
//        if (imagePath != null) {
//            filePath = imagePath // 현재 파일 경로를 초기화
//            binding.resultImageView.load(File(filePath))
//        }
//
//        // 2. "다시 찍기" 버튼을 누르면 launchCamera() 함수를 호출
//        binding.retakeButton.setOnClickListener {
//            launchCamera()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("imagePath")

        if (imagePath != null) {
            filePath = imagePath
            binding.resultImageView.load(File(filePath))
            // 3. API 호출 대신 가짜 데이터로 목록을 즉시 표시
            uploadImageAndGetLabels(filePath)
        }

        binding.retakeButton.setOnClickListener {
            launchCamera()
        }
    }


    // 4. 가짜 데이터로 RecyclerView를 설정하는 함수
    private fun setupRecyclerViewWithMockData() {
        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = FoodLabelAdapter(mockFoodLabels)
        }
    }



//    // 2. 이미지를 서버로 업로드하고 라벨을 받아오는 함수
    private fun uploadImageAndGetLabels(imagePath: String) {
        binding.progressBar.visibility = View.VISIBLE // 로딩 시작
        binding.foodRecyclerView.visibility = View.GONE // 목록 숨김

        lifecycleScope.launch {
            try {
                val file = File(imagePath)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                //val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val imageUri = Uri.fromFile(File(imagePath))

                val response = foodRepository.classifyFoodImageByYolo(this@ResultActivity, imageUri)

                response.onSuccess { response ->
                    // YOLO 응답에서 음식 이름 리스트 추출
                    val foodLabels = response.classificationResults.map { it.name }
                    setupRecyclerView(foodLabels)
                }.onFailure { exception ->
                    Log.e("API_ERROR", "Error: ${exception.message}")
                    // 오류 발생 시 가짜 데이터로 대체
                    setupRecyclerViewWithMockData()
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE // 로딩 종료
                binding.foodRecyclerView.visibility = View.VISIBLE // 목록 표시
            }
        }
    }

    // 실제 데이터로 RecyclerView 설정
    private fun setupRecyclerView(foodLabels: List<String?>) {
        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = FoodLabelAdapter(foodLabels)
        }
    }


    // 3. MainActivity에 있던 카메라 실행 함수를 그대로 가져옴
    private fun launchCamera() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        filePath = file.absolutePath // 새로 찍을 사진의 경로를 업데이트
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.vs.fileprovider",
            file
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        requestCameraFileLauncher.launch(intent)
    }
}