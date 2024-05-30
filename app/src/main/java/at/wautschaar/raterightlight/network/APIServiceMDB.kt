package at.wautschaar.raterightlight.network

import at.wautschaar.raterightlight.model.MovieResponse
import at.wautschaar.raterightlight.model.TVResponse
import at.wautschaar.raterightlight.model.TrendingMovieResponse
import at.wautschaar.raterightlight.model.TrendingTVResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY = "00297a2d23ae92ff00ab9ec2c9458711"
private const val API_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIwMDI5N2EyZDIzYWU5MmZmMDBhYjllYzJjOTQ1ODcxMSIsInN1YiI6IjY2NDVkMmExMDdmOTg5MTFkNTk1MzU5MyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.AWHQ4umhCWbwv0Wxs0BmNsLzBLCuL2YKhqHhCYUVQ_0"

val apiKeyInterceptor = Interceptor { chain ->
    val originalRequest: Request = chain.request()
    val originalUrl = originalRequest.url
    val url = originalUrl.newBuilder()
        .addQueryParameter("api_key", API_KEY)
        .build()
    val requestBuilder = originalRequest.newBuilder().url(url)
    val request = requestBuilder.build()
    chain.proceed(request)
}

val authTokenInterceptor = Interceptor { chain ->
    val originalRequest: Request = chain.request()
    val requestBuilder = originalRequest.newBuilder()
        .addHeader("Authorization", "Bearer $API_TOKEN")
    val request = requestBuilder.build()
    chain.proceed(request)
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(apiKeyInterceptor)
    .addInterceptor(authTokenInterceptor)
    .build()

var retrofitMDB = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface APIServiceMDB {
    @GET("search/movie")
    suspend fun getMovie(@Query("query") query: String): MovieResponse

    @GET("search/tv")
    suspend fun getTV(@Query("query") query: String): TVResponse

    @GET("trending/movie/day?language=en-US")
    suspend fun getTrendingMovie(): TrendingMovieResponse

    @GET("trending/tv/day?language=en-US")
    suspend fun getTrendingTV(): TrendingTVResponse
}

object APIMDB {
    val retrofitService: APIServiceMDB by lazy {
        retrofitMDB.create(APIServiceMDB::class.java)
    }
}
