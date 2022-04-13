package app.brainpool.nodesmobile.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import app.brainpool.nodesmobile.*
import app.brainpool.nodesmobile.data.localdatastore.MapTileNodes
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.networking.NodesMobileApi
import app.brainpool.nodesmobile.type.NotificationInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
import app.brainpool.nodesmobile.util.*
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import app.brainpool.nodesmobile.view.ui.MainActivity
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.vicpin.krealmextensions.save
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutionException
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
            locationArray.add(prop)
        }
        saveProperty(locationArray)
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
            email = userNodes?.email.toString()
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

    /*This method downloads and save map tiles to local storage*/
    override suspend fun downloadMapsQuery(
        context: MainActivity,
        mapId: String,
        fileName: String
    ): MutableList<MapTileNodes> {
//        val mapsArray = mutableListOf<MapTileNodes>()
        val listMapTiles =
            webService.getApolloClient(context.applicationContext).query(DownloadMapsQuery(mapId))
                .await().data?.downloadMaps
        if (listMapTiles?.size == 0) {
            if (WifiService.instance.isOnline())
                context.apply {
                    materialDialog(getString(R.string.map_not_found), "", getString(R.string.ok))
                    {
                        it.dismiss()
                    }
                }
        } else {
            GlobalScope.launch(Dispatchers.IO) {
//                val db = Realm.getDefaultInstance()
//                val listDbMapTiles = db.where(MapTileNodes::class.java)
//                    .equalTo("mapId", mapId).findAll()
//                /* already save in local db (but can/cannot be downloaded)*/
//                if (listMapTiles != null) {
//                    if (listDbMapTiles.size != listMapTiles.size) {
//                        val currentIdNum: Number? =
//                            db.where(MapTileNodes::class.java).max("id")
//                        for ((i, map) in listMapTiles.withIndex()) {
//                            val nextId: Int = if (currentIdNum == null) {
//                                i + 1
//                            } else {
//                                currentIdNum.toInt() + (i + 1)
//                            }
//            //                            val count = db.where(MapTileNodes::class.java)
//            //                                .equalTo("mapId", mapId).equalTo("link", map?.link.toString())
//            //                                .count()
//            //                            if (count.toInt() == 0) {
//                            val mapTile = MapTileNodes().apply {
//                                this.mapId = mapId
//                                fName = fileName
//                                id = nextId
//                                link = map?.link.toString()
//                                isDownloaded = false
//                            }
//                            mapsArray.add(mapTile)
//            //                            }
//                        }
//                        saveMapList(mapsArray)
//                    } else {
//                        for (map in listDbMapTiles) {
//                            if (map.isDownloaded == false) {
//                                mapsArray.add(map)
//                            }
//                        }
//                    }
//                }

                if (listMapTiles != null && WifiService.instance.isOnline()) {
                    val folderName = if (fileName.contains("."))
                        fileName.split(".")[0]
                    else
                        ""
                    val mapDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .toString() + "/.NodesMobile/" + folderName
                    )
                    val count = getAllImageFilesInFolder(mapDir)
                    Log.v(
                        TAG,
                        "Total Images: $count & from api: ${listMapTiles.size}"
                    )
                    if (listMapTiles.size != count) {
                        Log.v(TAG, "Downloading Map tiles")
                        try {
                            context.runOnUiThread {
                                Snackbar.make(
                                    context.binding.rvFooter,
                                    R.string.downloading,
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        for (p in listMapTiles) {
                            var fName = p?.link.toString()
                            /*directory structure in fName including imageName*/
                            fName =
                                fName.substring(fName.indexOf("maptiles/") + 9)//2D3UYW06VNRV6D/22/3859879/1692547.png
                            val fileTileImage = getFile(fName)
                            if (fileTileImage == null || !fileTileImage.exists()) {
                                try {
                                    val manager = WorkManager.getInstance(context)
                                    if (!isScheduled(manager, p?.link.toString())) {
                                        val downloadWork =
                                            OneTimeWorkRequest.Builder(DownloadImageWorker::class.java)
                                                .addTag(p?.link.toString())
                                        val data = Data.Builder()
                                        data.putString("fName", fName)
                                        data.putString("link", p?.link)
                                        downloadWork.setInputData(data.build())
                                        manager.enqueue(downloadWork.build())
                                    } else {
                                        Log.v(TAG, "Already scheduled")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    if (p != null) {
                                        Log.v(TAG, "Error:${p.link}")
                                    }
                                }
                            }
//                                else {
//                                    updateMapTileDownload(MapTileNodes().apply {
//                                        link = p?.link.toString()
//                                        this.mapId = mapId
//                                    })
//                                }
                        }
                        Log.v(TAG, "Downloading Map tiles complete")
                    }
                }
            }
        }
        return mutableListOf()
    }

    private fun isScheduled(manager: WorkManager, fName: String): Boolean {
        val statuses: ListenableFuture<List<WorkInfo>> =
            manager.getWorkInfosForUniqueWork(fName)
        return try {
            var running = false
            val workInfoList: List<WorkInfo>
            workInfoList = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING ||
                        state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

//    override fun updateMapTileDownload(m: MapTileNodes) {
//        val db = Realm.getDefaultInstance()
//        val mapTile = db.where(MapTileNodes::class.java)
//            .equalTo("mapId", m.mapId).equalTo("link", m.link.toString()).findFirst()
//
//        if (mapTile != null) {
//            val mapTile = MapTileNodes().apply {
//                this.mapId = mapTile.mapId
//                fName = mapTile.fName
//                id = mapTile.id
//                link = mapTile.link.toString()
//                isDownloaded = true
//            }
//
//            db.executeTransactionAsync {
//                it.copyToRealmOrUpdate(
//                    mapTile
//                )
//            }
//        }
//    }

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

    override fun saveProperty(p: List<Property>) {
        val db = Realm.getDefaultInstance()
        db.executeTransactionAsync {
            it.copyToRealmOrUpdate(p)
        }
    }

    override suspend fun getAllProperties(): MutableList<Property> {
        val db = Realm.getDefaultInstance()
        val proprtyList = db.where(Property::class.java).findAll()
        return proprtyList
    }

    override suspend fun getUserProfile(): UserNodes {
        val db = Realm.getDefaultInstance()
        val user = db.where(UserNodes::class.java).isNotEmpty("id").findFirst() ?: UserNodes()
        return UserNodes().apply {
            id = user.id
            email = user.email
            firstname = user.firstname.toString()
            lastname = user.lastname.toString()
            isSuperadmin = user.isSuperadmin
            licenseNumberId = user.licenseNumberId
            licenseNumberName = user.licenseNumberName
            role = user.role
            defPropertyId = user.defPropertyId
            imei = user.imei
            timeInterval = user.timeInterval
            radius = user.radius
        }
    }

    override fun saveUser(user: UserNodes) {
        val db = Realm.getDefaultInstance()
        db.executeTransactionAsync {
            UserNodes().apply {
                id = user.id
                email = user.email
                firstname = user.firstname.toString()
                lastname = user.lastname.toString()
                isSuperadmin = user.isSuperadmin
                licenseNumberId = user.licenseNumberId
                licenseNumberName = user.licenseNumberName
                role = user.role
                defPropertyId = user.defPropertyId
                imei = user.imei
                timeInterval = user.timeInterval
                radius = user.radius
            }.save()
        }
    }

    override fun updatePropertyNotification(pId: Property): Property? {
        val db = Realm.getDefaultInstance()
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
        return prop
    }

    override suspend fun getProperty(propertyId: String): Property {
        val db = Realm.getDefaultInstance()
        val list = db.where(Property::class.java).equalTo("id", propertyId)
            .findFirst() ?: Property()
        return list
    }

//    override fun saveMapList(list: List<MapTileNodes>) {
//        val db = Realm.getDefaultInstance()
//        db.executeTransactionAsync {
//            it.copyToRealmOrUpdate(list)
//        }
//    }

//    override suspend fun getAllMaps(mapId: String): MutableList<MapTileNodes> {
//        val db = Realm.getDefaultInstance()
//        val list = db.where(MapTileNodes::class.java).equalTo("mapId", mapId)
//            .findAll() ?: ArrayList()
//        return list
//    }

//    override suspend fun getAllMapsToBeDownload(mapId: String): MutableList<MapTileNodes> {
//        val db = Realm.getDefaultInstance()
//        val list = db.where(MapTileNodes::class.java).equalTo("mapId", mapId)
//            .equalTo("isDownloaded", false)
//            .findAll() ?: ArrayList()
//        return list
//    }
}