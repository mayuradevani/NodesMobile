package app.brainpool.nodesmobile.data.contract

import app.brainpool.nodesmobile.util.Either
import app.brainpool.nodesmobile.util.MyException
import dubai.business.womencouncil.data.models.LoginRS

interface LoginContract {
    suspend fun login(
        email: String
    ): Either<MyException, LoginRS>
}