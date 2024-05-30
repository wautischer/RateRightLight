package at.wautschaar.raterightlight.model

import kotlinx.serialization.Serializable

@Serializable
data class TV(
    val id: String,
    val original_language: String?,
    val original_name: String?,
    val overview: String?,
    val poster_path: String?,
    val first_air_date: String?
)

@Serializable
data class TVResponse(
    val results: List<TV>
)

@Serializable
data class tvItem(
    val id: String,
    val original_language: String,
    val original_name: String,
    val overview: String,
    val poster_path: String,
    val first_air_date: String
)