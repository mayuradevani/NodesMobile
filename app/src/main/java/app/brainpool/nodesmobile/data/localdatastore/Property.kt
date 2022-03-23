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
//    var centerLat: Double = 0.0
//    var centerLong: Double = 0.0
//    var mapTileFolder: String = ""

    override fun toString(): String {
        return name
    }
}