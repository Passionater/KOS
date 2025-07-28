package com.example.vs

import com.google.gson.annotations.SerializedName

/**
 * 음식 정보 데이터 클래스
 */
data class FoodInfo(
    @SerializedName("fi_id")
    val id: Long,
    
    @SerializedName("fi_name")
    val name: String?,
    
    @SerializedName("fi_category")
    val category: String?,
    
    @SerializedName("fi_category1")
    val category1: String?,
    
    @SerializedName("fi_onetime_offer")
    val onetimeOffer: Long?,
    
    @SerializedName("fi_calorie")
    val calorie: Double?,
    
    @SerializedName("fi_protein")
    val protein: Double?,
    
    @SerializedName("fi_fat")
    val fat: Double?,
    
    @SerializedName("fi_carbohydrate")
    val carbohydrate: Double?,
    
    @SerializedName("fi_totalSugar")
    val totalSugar: Double?,
    
    @SerializedName("fi_totalDietaryFiber")
    val totalDietaryFiber: Double?,
    
    @SerializedName("fi_giRate")
    val giRate: Long?,
    
    @SerializedName("fi_delYn")
    val delYn: String?
)

/**
 * 일반 이미지 분류 응답 데이터 클래스
 */
data class FoodClassificationResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("image_path")
    val imagePath: String,
    
    @SerializedName("success")
    val success: String,
    
    @SerializedName("accuracy")
    val accuracy: Double,
    
    @SerializedName("insert_id")
    val insertId: Long,
    
    @SerializedName("predicted_index")
    val predictedIndex: Int,
    
    @SerializedName("confidence")
    val confidence: Double,
    
    @SerializedName("predicted_class")
    val predictedClass: String,

    @SerializedName("classification_results")
    val classificationResults: List<FoodInfo>
)

/**
 * YOLO 기반 분류 응답 데이터 클래스
 */
data class YoloClassificationResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("original_image_path")
    val originalImagePath: String,

    @SerializedName("success_code")
    val success_code: String,
    
    @SerializedName("success")
    val success: String,
    
    @SerializedName("overall_accuracy")
    val overallAccuracy: Double,
    
    @SerializedName("insert_id")
    val insertId: Long,
    
    @SerializedName("detected_objects_count")
    val detectedObjectsCount: Int,
    
    @SerializedName("classificationResults")
    val classificationResults: List<FoodInfo>
)

/**
 * 개별 분류 결과 데이터 클래스
 */
data class ClassificationResult(
    @SerializedName("crop_index")
    val cropIndex: Int,
    
    @SerializedName("predicted_class")
    val predictedClass: String,
    
    @SerializedName("predicted_index")
    val predictedIndex: Int,
    
    @SerializedName("classification_confidence")
    val classificationConfidence: Double,
    
    @SerializedName("yolo_confidence")
    val yoloConfidence: Double,
    
    @SerializedName("bbox")
    val bbox: List<Int>,
    
    @SerializedName("crop_image_path")
    val cropImagePath: String
)

/**
 * API 에러 응답 데이터 클래스
 */
data class ApiErrorResponse(
    @SerializedName("error")
    val error: String
)
