package at.wautschaar.raterightlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: String,
    val original_language: String?,
    val title: String?,
    val overview: String?,
    val poster_path: String?,
    val release_date: String?
)

@Serializable
data class MovieResponse(
    val results: List<Movie>
)

@Serializable
data class TrendingMovieResponse(
    val results: List<Movie>
)

@Serializable
data class movieItem(
    val id: String,
    val original_language: String,
    val title: String,
    val overview: String,
    val poster_path: String,
    val release_date: String
)
