package com.example.vs

import NewsAdapter
import NewsItem
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vs.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var filePath: String
    private lateinit var newsAdapter: NewsAdapter

    // 1. 권한 요청 결과 처리를 위한 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCamera() // 권한을 허용하면 카메라 실행
        }
    }

    // 2. 카메라 촬영 결과 처리를 위한 런처
    private val requestCameraFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 사진 촬영을 성공적으로 마쳤는지 확인 (result.resultCode == RESULT_OK)
        if (result.resultCode == RESULT_OK) {
            // ResultActivity로 이동하는 인텐트 생성
            val intent = Intent(this, ResultActivity::class.java)
            // 인텐트에 사진 파일 경로(filePath)를 추가해서 전달
            intent.putExtra("imagePath", filePath)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. 버튼 클릭 리스너를 권한 확인 로직으로 변경
        binding.cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        setupRecyclerView()
        fetchNews()
    }

    // 4. 카메라 권한을 확인하고 요청하는 함수
    private fun checkCameraPermission() {
        when {
            // 권한이 이미 허용된 경우, 바로 카메라 실행
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            // 권한이 명시적으로 거부된 경우 (나중에 설명 UI 추가 가능)
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // 권한이 왜 필요한지 설명해주는 기능 추가
            }
            // 그 외의 경우, 권한 요청 팝업을 띄움
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // 5. 기존 카메라 실행 코드를 별도의 함수로 분리
    private fun launchCamera() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        filePath = file.absolutePath
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.vs.fileprovider",
            file
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        requestCameraFileLauncher.launch(intent)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.newsRecyclerView.adapter = newsAdapter
    }

    private fun fetchNews() {
        lifecycleScope.launch(Dispatchers.IO) {
            val newsList = mutableListOf<NewsItem>()
            val urls = listOf(
                "https://www.dangnyoshinmun.co.kr/news/article.html?no=24409",
                "https://www.dangnyoshinmun.co.kr/news/article.html?no=24408"
            )
            try {
                for (url in urls) {
                    val doc = Jsoup.connect(url).get()
                    val title = doc.select("meta[property=og:title]").attr("content")
                    val imageUrl = doc.select("meta[property=og:image]").attr("content")
                    if (title.isNotEmpty() && imageUrl.isNotEmpty()) {
                        newsList.add(NewsItem(title, imageUrl, url))
                    }
                }
            } catch (e: Exception) {
                Log.e("FetchNewsError", "뉴스 크롤링 중 오류 발생", e)
            }

            withContext(Dispatchers.Main) {
                newsAdapter = NewsAdapter(newsList)
                binding.newsRecyclerView.adapter = newsAdapter
            }
        }
    }
}