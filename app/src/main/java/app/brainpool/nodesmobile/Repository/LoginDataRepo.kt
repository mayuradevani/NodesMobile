package app.brainpool.nodesmobile.Repository

import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.data.contract.LoginContract
import app.brainpool.nodesmobile.util.Either
import app.brainpool.nodesmobile.util.MyException
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import dubai.business.womencouncil.data.models.LoginRS
import dubai.business.womencouncil.data.service.NetworkService

class LoginDataRepo constructor(private val networkService: NetworkService) : BaseRepository(),
    LoginContract {

    override suspend fun login(email: String): Either<MyException, LoginRS> {
        return either(tag = "Login") {
            networkService.login(email).also {
                Prefs.putString(PrefsKey.AUTH_KEY, it.authKey)
                Prefs.putString(PrefsKey.USER_ID, it.userRS.city)
                Prefs.putString(PrefsKey.USER, Gson().toJson(it.userRS))
            }
        }
    }
}