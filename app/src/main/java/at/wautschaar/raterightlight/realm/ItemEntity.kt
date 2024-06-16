package at.wautschaar.raterightlight.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


open class ItemEntity: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var contentId: String = ""
    var contentTye: String = ""
    var contentTitle: String = ""
    var contentInfo: String = ""
    var contentImg: String = ""

}