package at.wautschaar.raterightlight.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String?,
    val title: String,
    val authors: List<String>?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val categories: List<String>?,
    val language: String?,
    val imageUrl: String?
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        return title.contains(query, ignoreCase = true)
    }
}

@Serializable
data class BookResponse(
    val items: List<BookItem>
)

@Serializable
data class BookItem(
    val id: String,
    @SerialName("volumeInfo")
    val volumeInfo: VolumeInfo
)

@Serializable
data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val description: String,
    val imageLinks: ImageLinks,
    val publishedDate: String,
    val language: String,
    val pageCount: Int,
    val categories: List<String>
)

@Serializable
data class ImageLinks(
    @SerialName("smallThumbnail")
    val smallThumbnail: String,
    val thumbnail: String
)
