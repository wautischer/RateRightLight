package at.wautschaar.raterightlight.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val authors: List<String>,
    val publishedDate: String,
    val description: String,
    val pageCount: Int,
    val categories: List<String>,
    val language: String,
    val imageUrl: String
)