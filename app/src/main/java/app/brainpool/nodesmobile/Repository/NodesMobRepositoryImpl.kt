package app.brainpool.nodesmobile.Repository

import android.content.Context
import app.brainpool.nodesmobile.*
import app.brainpool.nodesmobile.networking.NodesMobileApi
import app.brainpool.nodesmobile.type.NotificationInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import javax.inject.Inject

class NodesMobRepositoryImpl @Inject constructor(
    private val webService: NodesMobileApi
) :
    NodesMobRepository {
    override suspend fun launguageCodeData(context: Context): Response<LanguageCodeDataQuery.Data> {
        return webService.getApolloClient(context).query(LanguageCodeDataQuery()).await()
    }


    override suspend fun getAllProperties(
        context: Context
    ): Response<GetAllPropertiesQuery.Data> {
        return webService.getApolloClient(context)
            .query(GetAllPropertiesQuery())
            .await()
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

    override suspend fun getUserProfile(context: Context): Response<GetUserProfileQuery.Data> {
        return webService.getApolloClient(context).query(GetUserProfileQuery()).await()
    }

    override suspend fun getAllMapsByPropertyIdQuery(
        context: Context, propertyId: String
    ): Response<GetAllMapsByPropertyIdQuery.Data> {
        return webService.getApolloClient(context).query(GetAllMapsByPropertyIdQuery(propertyId))
            .await()
    }

    override suspend fun downloadMapsQuery(
        context: Context,
        fileName: String
    ): Response<DownloadMapsQuery.Data> {
        return webService.getApolloClient(context).query(DownloadMapsQuery(fileName)).await()
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
}