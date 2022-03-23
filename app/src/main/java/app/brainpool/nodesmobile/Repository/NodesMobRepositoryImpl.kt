package app.brainpool.nodesmobile.Repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import app.brainpool.nodesmobile.*
import app.brainpool.nodesmobile.data.localdatastore.MapTileNodes
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.networking.NodesMobileApi
import app.brainpool.nodesmobile.type.NotificationInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.vicpin.krealmextensions.save
import io.realm.Realm
import javax.inject.Inject

class NodesMobRepositoryImpl @Inject constructor(
    private val webService: NodesMobileApi
) : NodesMobRepository {

    override suspend fun getAllProperties(
        context: Context
    ): MutableList<Property> {
        val locationArray = mutableListOf<Property>()
        val proprtyList = webService.getApolloClient(context)
            .query(GetAllPropertiesQuery())
            .await().data?.getAllProperties ?: ArrayList()

        for (p in proprtyList) {
//            var folder = ""
//            if (p?.propertyFile?.filename?.contains(".jpg") == true)
//                folder = p.propertyFile.filename.split(".jpg").get(0)
//            else
//                folder = p?.propertyFile?.filename?.split(".tiff")?.get(0)
//                    .toString()
            val prop = Property().apply {
                id = p?.id.toString()
                name = p?.name.toString()
//                centerLat = p.center?.coordinates?.get(1)!!
//                centerLong = p.center.coordinates.get(0)!!
                fileName = p?.defaultMap?.filename.toString()
                mapId = p?.defaultMap?.id.toString()
                fileUrl = p?.defaultMap?.fileUrl.toString()
//                mapTileFolder = folder
            }
            saveProperty(prop)
            locationArray.add(prop)
        }

//        for ((i, p) in proprtyList!!.withIndex()) {
//            locationArray.add(p?.name.toString())
////            if (pIdLastSelected == p?.id)
////                itemClick = i
//        }
        return locationArray
    }


    override suspend fun loginWithEmail(
        context: Context,
        email: String
    ): Response<LoginMutation.Data> {
        return webService.getApolloClient(context).mutate(LoginMutation(email = email)).await()
    }

    override suspend fun updateOrStoreNotificationToken(
        context: Context,
        data: NotificationInput
    ): Response<UpdateOrStoreNotificationTokenMutation.Data> {
        return webService.getApolloClient(context)
            .mutate(UpdateOrStoreNotificationTokenMutation(notificationInput = data)).await()
    }

    override suspend fun getUserProfile(context: Context): UserNodes {
        val userNodes = webService.getApolloClient(context).query(GetUserProfileQuery())
            .await().data?.getUserProfile
        val user = UserNodes().apply {
            id = userNodes?.id.toString()
            firstname = userNodes?.firstname.toString()
            lastname = userNodes?.lastname.toString()
            isSuperadmin = userNodes?.isSuperadmin
            licenseNumberId = userNodes?.licensenumber?.id.toString()
            licenseNumberName = userNodes?.licensenumber?.name.toString()
            role = userNodes?.role?.name
            defPropertyId = userNodes?.property?.id
            imei = userNodes?.imei
            timeInterval = userNodes?.timeInterval
            radius = userNodes?.radius
        }
        saveUser(user)
        return user

    }

//    override suspend fun getAllMapsByPropertyIdQuery(
//        context: Context, propertyId: String
//    ): Property {
//        val response =
//            webService.getApolloClient(context).query(GetAllMapsByPropertyIdQuery(propertyId))
//                .await()
//        val proprtyList = response.data?.getAllMapsByPropertyId
//        if (proprtyList != null) {
//            for (p in proprtyList) {
//                if (p?.isDefault == true) {
//                    if (!p.mapTileFile?.filename.isNullOrEmpty()) {
//
////                        Prefs.putString(
////                            PrefsKey.MAP_TILE_FILE_NAME,
////                            p.mapTileFile?.filename
////                        )
//
////                        Prefs.putString(
////                            PrefsKey.MAP_TILE_FOLDER,
////                            folder
////                        )
//
//                        //center
////                        Prefs.putDouble(
////                            PrefsKey.MAP_CENTER_LATI,
////                            p.center?.coordinates?.get(1)!!
////                        )
////                        Prefs.putDouble(
////                            PrefsKey.MAP_CENTER_LONGI,
////                            p.center?.coordinates?.get(0)!!
////                        )
//                        val property = Property().apply {
//                            id = propertyId
//                            centerLat = p.center?.coordinates?.get(1)!!
//                            centerLong = p.center.coordinates.get(0)!!
//                            mapTileFileName = p.mapTileFile?.filename.toString()
////                            mapTileFolder = folder
//                        }
//
////                                    //south west
////                                    Prefs.putDouble(
////                                        PrefsKey.MAP_SOUTHWEST_LATI,
////                                        p.center?.coordinates?.get(1)!!
////                                    )
////                                    Prefs.putDouble(
////                                        PrefsKey.MAP_SOUTHWEST_LONGI,
////                                        p.center?.coordinates?.get(0)!!
////                                    )
////
////                                    //north east
////                                    Prefs.putDouble(
////                                        PrefsKey.MAP_NORTHEAST_LATI,
////                                        p.center?.coordinates?.get(1)!!
////                                    )
////                                    Prefs.putDouble(
////                                        PrefsKey.MAP_NORTHEAST_LONGI,
////                                        p.center?.coordinates?.get(0)!!
////                                    )
//                        updateMapTileFile(property)
//                        return property
//                    }
//                }
//            }
//        }
//        return Property()
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun downloadMapsQuery(
        context: Context,
        mapIdV: String,
        fileName: String
    ): MutableList<MapTileNodes> {
        val mapsArray = mutableListOf<MapTileNodes>()
        val mapTileList = webService.getApolloClient(context).query(DownloadMapsQuery(mapIdV))
            .await().data?.downloadMaps
        if (mapTileList != null) {
//          As Realm support only 100 threads at a time
//            val partitionedList: MutableCollection<MutableList<DownloadMapsQuery.DownloadMap?>> =
//                IntStream.range(0, mapTileList.size)
//                    .boxed()
//                    .collect(
//                        Collectors.groupingBy(
//                            { partition -> partition / 100 },
//                            Collectors.mapping(
//                                { elementIndex -> mapTileList.get(elementIndex) },
//                                Collectors.toList()
//                            )
//                        )
//                    ).values
//            for (list in partitionedList) {
                val currentIdNum: Number? =
                    Realm.getDefaultInstance().where(MapTileNodes::class.java).max("id")
                for ((i, map) in mapTileList.withIndex()) {
                    val nextId: Int = if (currentIdNum == null) {
                        i + 1
                    } else {
                        currentIdNum.toInt() + (i + 1)
                    }
                    val mapTile = MapTileNodes().apply {
                        mapId = mapIdV
                        fName = fileName
                        id = nextId
                        link = map?.link.toString()
                    }
                    mapsArray.add(mapTile)
                }
                saveMap(mapsArray)
//                Thread.sleep(5000)
//            }
        }
        return mapsArray
    }

    override suspend fun createTrackerPositionData(
        context: Context,
        data: TrackerPositionInput
    ): Response<CreateTrackerPositionDataMutation.Data> {
        return webService.getApolloClient(context)
            .mutate(CreateTrackerPositionDataMutation(data = data)).await()
    }

    override suspend fun logout(context: Context): Response<LogoutUserDataQuery.Data> {
        return webService.getApolloClient(context).query(LogoutUserDataQuery())
            .await()
    }

    override suspend fun updateStatusTrackerData(
        context: Context,
        deviceId: String, isActive: Boolean
    ): Response<UpdateStatusTrackerDataMutation.Data> {
        return webService.getApolloClient(context)
            .mutate(UpdateStatusTrackerDataMutation(deviceId, isActive))
            .await()
    }

    override fun saveProperty(p: Property) {

        val db = Realm.getDefaultInstance()
        db.executeTransactionAsync {
            Property().apply {
                id = p.id
                name = p.name
                fileName = p.fileName
                mapId = p.mapId
                fileUrl = p.fileUrl
            }.save()
        }
//        val realm = Realm.getInstance(mRealmConfiguration)
//        realm.beginTransaction()
//        val realmPage = realm.createObject(
//            Property::class.java
//        )
//        realmPage.id = page.id
//        realm.copyToRealmOrUpdate(realmPage)
//        realm.commitTransaction()
//        realm.close()
    }

    override suspend fun getAllProperties(): MutableList<Property> {
//        val db = Realm.getDefaultInstance()
//        val page = db.where(
//            Property::class.java
//        ) //                        .equalTo("query", query)
//            .findFirst()
//        if (page != null && page.isLoaded && page.isValid) {
//            subscriber.onNext(db.copyFromRealm(page))
//        } else {
//            Observable.empty<Any>()
//        }
//        subscriber.onCompleted()
//        db.close()
        var proprtyList = Realm.getDefaultInstance().where(Property::class.java).findAll()
//        return suspendCoroutine {
//                continuation ->
//            Realm.getDefaultInstance().executeTransactionAsync({ realm ->
//                Realm.getDefaultInstance().where(Property::class.java).findAll()
//                    .map { proprtyList }
//            }, {
//                continuation.resumeWith(Result.success(proprtyList))
//            }, { exception ->
//                continuation.resumeWith(Result.failure(exception))
//            })
//        }
        return proprtyList
    }

    override suspend fun getUserProfile(): UserNodes {
        return Realm.getDefaultInstance().where(UserNodes::class.java).findFirst() ?: UserNodes()
//        equalTo("id",)
    }

    override fun saveUser(userNodes: UserNodes) {
        val db = Realm.getDefaultInstance()
        db.executeTransactionAsync {
            UserNodes().apply {
                id = userNodes.id.toString()
                firstname = userNodes.firstname.toString()
                lastname = userNodes.lastname.toString()
                isSuperadmin = userNodes.isSuperadmin
                licenseNumberId = userNodes.licenseNumberId
                licenseNumberName = userNodes.licenseNumberName
                role = userNodes.role
                defPropertyId = userNodes.defPropertyId
                imei = userNodes.imei
                timeInterval = userNodes.timeInterval
                radius = userNodes.radius
            }.save()
        }
    }

    override fun updatePropertyNotification(pId: Property): Property? {
//        var folder = ""
//        if (p.mapTileFileName.contains(".jpg"))
//            folder = p.mapTileFileName.split(".jpg").get(0)
//        else
//            folder = p.mapTileFileName?.split(".tiff")?.get(0)
//                .toString()

//        prop?.mapTileFolder = p.mapTileFolder
//        prop?.centerLat = p.centerLat
//        prop?.centerLong = p.centerLong
//        return Realm.getDefaultInstance().copyToRealmOrUpdate(prop)
        val db = Realm.getDefaultInstance()
//        val mapEx = db.where(Property::class.java).equalTo("id", p.id).findFirst()
        val prop =
            db.where(Property::class.java).equalTo("id", pId.id).findFirst()
        val fNameP = prop?.name.toString()
        if (prop != null)
            db.executeTransactionAsync {
                it.insertOrUpdate(Property().apply {
                    fileName = pId.fileName
                    name = fNameP
                    id = pId.id
                    fileUrl = pId.fileUrl
                    mapId = pId.mapId
                })
            }
        db.close()
        return prop
    }

    override suspend fun getProperty(propertyId: String): Property {
        return Realm.getDefaultInstance().where(Property::class.java).equalTo("id", propertyId)
            .findFirst() ?: Property()
    }

    override fun saveMap(list: List<MapTileNodes>) {
        val db = Realm.getDefaultInstance()
//        val mapEx = db.where(MapTileNodes::class.java)
//            .equalTo("fName", map.fName).equalTo("link", map.link)
//            .findFirst()
//        if (mapEx == null)
            db.executeTransactionAsync {
                it.copyToRealmOrUpdate(list)
//                it.insertOrUpdate(MapTileNodes().apply {
//                    mapId = map.mapId
//                    fName = map.fName
//                    link = map.link
//                })
//            MapTileNodes().apply {
//                id = map.id
//                fName = map.fName
//                link = map.link
//            }.save()
            }
        db.close()
    }

    override suspend fun getAllMaps(mapId: String): MutableList<MapTileNodes> {
        return Realm.getDefaultInstance().where(MapTileNodes::class.java).equalTo("mapId", mapId)
            .findAll() ?: ArrayList()
    }
}