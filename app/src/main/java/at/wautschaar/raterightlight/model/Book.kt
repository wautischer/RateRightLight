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

data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

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

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)
