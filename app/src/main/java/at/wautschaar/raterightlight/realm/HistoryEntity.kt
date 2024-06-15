package at.wautschaar.raterightlight.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class HistoryEntity : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var contentType: String = ""
    var contentId: String = ""
    var timestamp: Long = System.currentTimeMillis()
}
