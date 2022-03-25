package app.brainpool.nodesmobile.data_interface

import app.brainpool.nodesmobile.repository.NodesMobRepository
import app.brainpool.nodesmobile.repository.NodesMobRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    @Binds
    @ViewModelScoped
    abstract fun bindrepository(repo: NodesMobRepositoryImpl): NodesMobRepository
}