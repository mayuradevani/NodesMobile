package app.brainpool.nodesmobile.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.util.doInBackground
import app.brainpool.nodesmobile.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NodesMobRepository,
) : ViewModel() {

}