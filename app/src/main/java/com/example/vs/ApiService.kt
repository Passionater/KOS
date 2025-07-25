// ApiService.kt
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/detect") // FastAPI 서버의 엔드포인트 (예: /detect)
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<List<String>> // 서버로부터 라벨 이름 목록(List<String>)을 받음
}