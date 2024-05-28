package at.wautschaar.raterightlight.network

import at.wautschaar.raterightlight.model.MovieResponse
import at.wautschaar.raterightlight.model.TV
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY = "00297a2d23ae92ff00ab9ec2c9458711"

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

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(apiKeyInterceptor)
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
    suspend fun getTV(@Query("query") query: String): List<TV>
}

object APIMDB {
    val retrofitService: APIServiceMDB by lazy {
        retrofitMDB.create(APIServiceMDB::class.java)
    }
}
