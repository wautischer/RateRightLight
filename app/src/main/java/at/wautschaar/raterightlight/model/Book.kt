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
)

@Serializable
data class BookListResponse(
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
    val imageLinks: ImageLinks?,
    val publishedDate: String,
    val language: String,
    val pageCount: Int,
    val categories: List<String>
)

@Serializable
data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)

@Serializable
data class SingleBookResponse(
    val kind: String,
    val id: String,
    val etag: String,
    val selfLink: String,
    @SerialName("volumeInfo")
    val volumeInfo: VolumeInfo
)