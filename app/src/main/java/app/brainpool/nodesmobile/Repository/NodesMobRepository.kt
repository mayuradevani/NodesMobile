package app.brainpool.nodesmobile.Repository

import android.content.Context
import app.brainpool.nodesmobile.*
import app.brainpool.nodesmobile.data.localdatastore.MapTileNodes
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.type.NotificationInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
import com.apollographql.apollo.api.Response

interface NodesMobRepository {
    suspend fun loginWithEmail(context:Context,email: String): Response<LoginMutation.Data>
    suspend fun getAllProperties(context: Context): MutableList<Property>
    suspend fun getUserProfile(context: Context): UserNodes
//    suspend fun getAllMapsByPropertyIdQuery(context:Context,propertyId: String): Property
    suspend fun downloadMapsQuery(context:Context, mapId:String, fileName: String): MutableList<MapTileNodes>

    suspend fun updateOrStoreNotificationToken(context:Context,data: NotificationInput): Response<UpdateOrStoreNotificationTokenMutation.Data>
    suspend fun createTrackerPositionData(context:Context,data: TrackerPositionInput): Response<CreateTrackerPositionDataMutation.Data>
    suspend fun logout(context: Context): Response<LogoutUserDataQuery.Data>
    suspend fun updateStatusTrackerData(context: Context, deviceId:String,isActive:Boolean): Response<UpdateStatusTrackerDataMutation.Data>

    fun saveProperty(p: Property)
    suspend fun getAllProperties(): MutableList<Property>
    suspend fun getUserProfile(): UserNodes
    fun saveUser(user: UserNodes)
    suspend fun getProperty(propertyId: String):Property?
    fun saveMap(map: List<MapTileNodes>)
    suspend fun getAllMaps(mapId: String): MutableList<MapTileNodes>
    fun updatePropertyNotification(pId:Property):Property?
}