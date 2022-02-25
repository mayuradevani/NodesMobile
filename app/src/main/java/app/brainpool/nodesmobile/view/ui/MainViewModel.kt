package app.brainpool.nodesmobile.view.ui

import androidx.lifecycle.ViewModel
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NodesMobRepository,
) : ViewModel() {

}