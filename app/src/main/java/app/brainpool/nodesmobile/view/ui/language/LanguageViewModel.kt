package app.brainpool.nodesmobile.view.ui.language

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.LanguageCodeDataQuery
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.util.doInBackground
import app.brainpool.nodesmobile.view.state.ViewState
import com.apollographql.apollo.api.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repository: NodesMobRepository,
) : ViewModel() {

    private val _languageCodeList by lazy {
        MutableLiveData<ViewState<Response<LanguageCodeDataQuery.Data>>>()
    }
    val launguageCodeList: LiveData<ViewState<Response<LanguageCodeDataQuery.Data>>>
        get() = _languageCodeList

    fun queryLanguageList(context: Context) = viewModelScope.launch {
        doInBackground(_languageCodeList, "Error fetching languages") {
            repository.launguageCodeData(context)
        }
    }
}