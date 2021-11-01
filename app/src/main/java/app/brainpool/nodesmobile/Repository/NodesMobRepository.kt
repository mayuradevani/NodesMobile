package app.brainpool.nodesmobile.Repository

import app.brainpool.nodesmobile.LanguageCodeDataQuery
import app.brainpool.nodesmobile.LoginMutation
import com.apollographql.apollo.api.Response

interface NodesMobRepository {
    suspend fun queryLaunguageCodeData(): Response<LanguageCodeDataQuery.Data>
    suspend fun queryLoginWithEmail(email: String): Response<LoginMutation.Data>
}