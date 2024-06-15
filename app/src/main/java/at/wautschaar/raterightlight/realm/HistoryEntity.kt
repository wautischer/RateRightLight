package at.wautschaar.raterightlight.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlin.random.Random

open class HistoryEntity() : RealmObject {
    @PrimaryKey
    var id: String = Random(Integer.MAX_VALUE).toString()
    var contentType: String = ""
    var contentId: String = ""
    var timestamp: Long = System.currentTimeMillis()
}