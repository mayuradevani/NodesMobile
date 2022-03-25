package app.brainpool.nodesmobile.data.localdatastore

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Property : RealmObject() {
    @PrimaryKey
    lateinit var id: String
    var name: String = ""
    var mapId:String=""
    var fileName: String = ""
    var fileUrl:String=""
    override fun toString(): String {
        return name
    }
}