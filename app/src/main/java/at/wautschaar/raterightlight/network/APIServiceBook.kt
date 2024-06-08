package at.wautschaar.raterightlight.network

import at.wautschaar.raterightlight.model.BookResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


private const val BASE_URL = "https://www.googleapis.com/books/v1/"

var retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface APIService {
    @GET("volumes")
    suspend fun getBooks(@Query("q") query: String): BookResponse

    @GET("volumes/{bookId}")
    suspend fun getBookByID(@Path("bookId") bookId: String): BookResponse

}

object APIBook {
    val retrofitService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}