package com.example.vs

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Food API Repository 클래스
 * API 호출을 관리하는 Repository 패턴 구현
 */
class FoodRepository {
    
    private val apiService = ApiClient.foodApiService
    
    /**
     * 모든 음식 정보 조회
     */
    suspend fun getAllFoodInfo(): Result<List<FoodInfo>> {
        return try {
            val response = apiService.getAllFoodInfo()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 활성 음식 정보 조회
     */
    suspend fun getActiveFoodInfo(): Result<List<FoodInfo>> {
        return try {
            val response = apiService.getActiveFoodInfo()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 일반 이미지 분류
     */
    suspend fun classifyFoodImage(context: Context, imageUri: Uri): Result<FoodClassificationResponse> {
        return try {
            val imageFile = createTempFileFromUri(context, imageUri)
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            
            val response = apiService.classifyFoodImage(imagePart)
            
            // 임시 파일 삭제
            imageFile.delete()
            
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * YOLO 기반 이미지 분류
     */
    suspend fun classifyFoodImageByYolo(context: Context, imageUri: Uri): Result<YoloClassificationResponse> {
        return try {
            val imageFile = createTempFileFromUri(context, imageUri)
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            
            val response = apiService.classifyFoodImageByYolo(imagePart)
            
            // 임시 파일 삭제
            imageFile.delete()
            
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Uri에서 임시 파일 생성
     */
    private fun createTempFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return tempFile
    }
}
