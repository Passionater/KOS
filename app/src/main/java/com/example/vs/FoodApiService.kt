package com.example.vs

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Food API Service Interface
 * FastAPI 서버와 통신하기 위한 Retrofit 인터페이스
 */
interface FoodApiService {
    
    /**
     * 모든 음식 정보 조회
     */
    @GET("api/food/info")
    suspend fun getAllFoodInfo(): Response<List<FoodInfo>>
    
    /**
     * 삭제되지 않은 음식 정보 조회
     */
    @GET("api/food/infoByDelYn")
    suspend fun getActiveFoodInfo(): Response<List<FoodInfo>>
    
    /**
     * 일반 이미지 분류
     */
    @Multipart
    @POST("api/food/classifyFoodImage")
    suspend fun classifyFoodImage(
        @Part image: MultipartBody.Part
    ): Response<FoodClassificationResponse>
    
    /**
     * YOLO 기반 이미지 분류
     */
    @Multipart
    @POST("api/food/classifyFoodImageByYolo")
    suspend fun classifyFoodImageByYolo(
        @Part image: MultipartBody.Part
    ): Response<YoloClassificationResponse>
}
