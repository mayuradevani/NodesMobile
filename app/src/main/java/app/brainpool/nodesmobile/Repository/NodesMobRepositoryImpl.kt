package app.brainpool.nodesmobile.Repository

import app.brainpool.nodesmobile.LanguageCodeDataQuery
import app.brainpool.nodesmobile.LoginMutation
import app.brainpool.nodesmobile.networking.NodesMobileApi
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import javax.inject.Inject

class NodesMobRepositoryImpl @Inject constructor(
    private val webService: NodesMobileApi
) :
    NodesMobRepository {
    override suspend fun queryLaunguageCodeData(): Response<LanguageCodeDataQuery.Data> {
        return webService.getApolloClient().query(LanguageCodeDataQuery()).await()
    }

    override suspend fun queryLoginWithEmail(email: String): Response<LoginMutation.Data> {
        return webService.getApolloClient().mutate(LoginMutation(email = email)).await()
    }
}