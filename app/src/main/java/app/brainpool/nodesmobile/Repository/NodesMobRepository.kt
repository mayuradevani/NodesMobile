package app.brainpool.nodesmobile.Repository

import android.content.Context
import app.brainpool.nodesmobile.*
import app.brainpool.nodesmobile.type.NotificationInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
import com.apollographql.apollo.api.Response

interface NodesMobRepository {
    suspend fun loginWithEmail(context:Context,email: String): Response<LoginMutation.Data>
    suspend fun getAllProperties(context: Context): Response<GetAllPropertiesQuery.Data>
    suspend fun getUserProfile(context: Context): Response<GetUserProfileQuery.Data>
    suspend fun getAllMapsByPropertyIdQuery(context:Context,propertyId: String): Response<GetAllMapsByPropertyIdQuery.Data>
    suspend fun downloadMapsQuery(context:Context, fileName: String): Response<DownloadMapsQuery.Data>
    suspend fun updateOrStoreNotificationToken(context:Context,data: NotificationInput): Response<UpdateOrStoreNotificationTokenMutation.Data>
    suspend fun createTrackerPositionData(context:Context,data: TrackerPositionInput): Response<CreateTrackerPositionDataMutation.Data>
    suspend fun logout(context: Context): Response<LogoutUserDataQuery.Data>
    suspend fun updateStatusTrackerData(context: Context, deviceId:String,isActive:Boolean): Response<UpdateStatusTrackerDataMutation.Data>
}