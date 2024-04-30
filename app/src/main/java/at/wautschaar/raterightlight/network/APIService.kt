package at.wautschaar.raterightlight.network

import at.wautschaar.raterightlight.model.Book
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/books/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json {
        ignoreUnknownKeys = true
    }.asConverterFactory("application/json".toMediaType())
    ).baseUrl(BASE_URL).build()

interface APIService {
    @GET("volumes")
    suspend fun getBooks(@Query("q") query: String): List<Book>
}

object API {
    val retrofitService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}