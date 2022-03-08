package app.brainpool.nodesmobile.data_interface

import app.brainpool.nodesmobile.networking.NodesMobileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Singleton
    @Provides
    fun provideWebService() = NodesMobileApi()
}