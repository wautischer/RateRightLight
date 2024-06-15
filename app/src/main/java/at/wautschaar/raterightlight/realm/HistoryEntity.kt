package at.wautschaar.raterightlight.realm

import android.annotation.SuppressLint
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime

@SuppressLint("NewApi")
open class HistoryEntity : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var contentId: String = ""
    var contentTitle: String = ""
    var contentInfo: String = ""
    @Ignore
    var timestamp: LocalDateTime? = null

    init {
        if (timestamp == null) {
            timestamp = LocalDateTime.now()
        }
    }
}
