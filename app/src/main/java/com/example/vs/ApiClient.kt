package com.example.vs

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit API Client 싱글톤 클래스
 */
object ApiClient {
    
    // 서버 Base URL (실제 서버 IP로 변경 필요)
    private const val BASE_URL = "http://172.30.1.90:8000/"  // 실제 IP 주소로 변경
    
    // Logging Interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // OkHttp Client 설정
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Retrofit 인스턴스
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Service 인스턴스
    val foodApiService: FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }
}
