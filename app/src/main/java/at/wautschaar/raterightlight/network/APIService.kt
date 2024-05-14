package at.wautschaar.raterightlight.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/books/v1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json {
        ignoreUnknownKeys = true
    }.asConverterFactory("application/json".toMediaType())
    ).baseUrl(BASE_URL).build()

interface APIService {
    @GET("volumes")
    suspend fun getBooks(@Query("q") query: String): BookResponse
}

data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeInfo,
    val id: String
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val description: String,
    val imageLinks: ImageLinks,
    val publishedDate: String,
    val language: String,
    val pageCount: Int,
    val category: List<String>
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)

object API {
    val retrofitService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}