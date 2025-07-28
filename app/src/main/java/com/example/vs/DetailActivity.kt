package com.example.vs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vs.databinding.ActivityDetailBinding
import com.example.vs.databinding.ItemNutritionBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 화면에서 전달받은 음식 이름
        val foodName = intent.getStringExtra("foodName")
        binding.foodNameTextView.text = foodName

        // 닫기 버튼 클릭 시 현재 화면 종료
        binding.closeButton.setOnClickListener {
            finish()
        }

        // 가짜 데이터로 UI 채우기
        setupMockData()
    }

    private fun setupMockData() {
        // 각 영양소 정보 변수화
        val foodCarbohydrate = intent.getStringExtra("foodCarbohydrate")
        val foodProtein = intent.getStringExtra("foodProtein")
        val foodFat = intent.getStringExtra("foodFat")
        val foodGiRate = intent.getStringExtra("foodGiRate")
        val foodTotalDietaryFiber = intent.getStringExtra("foodTotalDietaryFiber")


        // 각 영양소 뷰에 접근하여 데이터 설정
        binding.carbs.apply {
            nutritionTitleTextView.text = "탄수화물"
            nutritionValueTextView.text = buildString {
                append(foodCarbohydrate)
                append("g")
            }
        }
        binding.protein.apply {
            nutritionTitleTextView.text = "단백질"
            nutritionValueTextView.text = buildString {
                append(foodProtein)
                append("g")
            }
        }
        binding.fat.apply {
            nutritionTitleTextView.text = "지방"
            nutritionValueTextView.text = buildString {
                append(foodFat)
                append("g")
            }
        }
        binding.fiber.apply {
            nutritionTitleTextView.text = "식이섬유"
            nutritionValueTextView.text = buildString {
                append(foodTotalDietaryFiber)
                append("g")
            }
        }
        binding.giValueTextView.text = buildString {
            append(foodGiRate)
            append("g")
        }
    }
}