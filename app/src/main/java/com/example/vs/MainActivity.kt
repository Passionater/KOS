package com.example.vs // 👈 1. 패키지 이름 변경

import NewsAdapter
import NewsItem
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.vs.databinding.ActivityMainBinding // 👈 2. 바인딩 import 변경
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var filePath: String
    private lateinit var newsAdapter: NewsAdapter

    val requestCameraFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 사진 촬영 후 로직 (현재는 비워둠)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraButton.setOnClickListener {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            filePath = file.absolutePath

            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.vs.fileprovider", // 👈 3. FileProvider 권한 이름 변경
                file
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            requestCameraFileLauncher.launch(intent)
        }
        setupRecyclerView()
        fetchNews()
    }
    private fun setupRecyclerView() {
        // 1. 어댑터를 초기화합니다 (처음에는 빈 목록으로).
        newsAdapter = NewsAdapter(emptyList())
        // 2. 리사이클러뷰에 레이아웃 매니저와 어댑터를 설정합니다.
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.newsRecyclerView.adapter = newsAdapter
    }

    private fun fetchNews() {
        // 3. 코루틴을 사용해 백그라운드에서 네트워크 작업을 수행합니다.
        lifecycleScope.launch(Dispatchers.IO) {
            val newsList = mutableListOf<NewsItem>()
            val urls = listOf(
                "https://www.dangnyoshinmun.co.kr/news/article.html?no=24409",
                "https://www.dangnyoshinmun.co.kr/news/article.html?no=24408"
            )

            try {
                for (url in urls) {
                    val doc = Jsoup.connect(url).get()
                    // 웹페이지의 meta 태그에서 제목과 이미지 URL을 추출합니다.
                    val title = doc.select("meta[property=og:title]").attr("content")
                    val imageUrl = doc.select("meta[property=og:image]").attr("content")
                    if (title.isNotEmpty() && imageUrl.isNotEmpty()) {
                        newsList.add(NewsItem(title, imageUrl, url))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 4. 메인 스레드에서 UI를 업데이트합니다.
            withContext(Dispatchers.Main) {
                newsAdapter = NewsAdapter(newsList)
                binding.newsRecyclerView.adapter = newsAdapter
            }
        }
    }
    // 이미지 크기를 계산하는 함수 (필요하다면 사용)
    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            var inputStream = contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}