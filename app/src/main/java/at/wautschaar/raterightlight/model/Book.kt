package at.wautschaar.raterightlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val publishedDate: String,
    val description: String
)