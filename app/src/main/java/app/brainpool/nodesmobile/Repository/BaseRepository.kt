package app.brainpool.nodesmobile.Repository

import app.brainpool.nodesmobile.util.Either
import app.brainpool.nodesmobile.util.MyException
import com.google.gson.Gson
import dubai.business.womencouncil.data.models.BaseRs
import retrofit2.HttpException
import java.net.UnknownHostException

open class BaseRepository {


    suspend fun <R> either(autoHandle: Boolean = true, tag: String = "", data: suspend () -> R): Either<MyException, R> {
        return try {
            Either.Right(data.invoke())
        } catch (e: Exception) {
            e.localizedMessage
            return Either.Left(convertToMyException(e, tag))
        }
    }

    private fun convertToMyException(e: Exception, tag: String): MyException {
        return when (e) {
            is HttpException -> convertHttpException(e, tag);
            is UnknownHostException -> MyException.NetworkErrorError(e)

            else -> MyException.UnKnownError(e)
        }
    }

    private fun convertHttpException(e: HttpException, tag: String): MyException {
        var message = ""
        var error = ""
        var status = ""
        try {
            val baseRs = Gson().fromJson(e.response()?.errorBody()?.string(), BaseRs::class.java)
            message = baseRs.message
            error = baseRs.error
            status = baseRs.status
        } catch (e: Exception) {
        }
        if (tag == "Login") {
            status.let {
                return MyException.InvalidCredentials(RuntimeException(it))
            }
        }
        return when (e.code()) {
            401 -> MyException.UnAuthenticate(e)
            422 -> {
                message.let {
                    if (!error.isNullOrBlank()) {
                        MyException.Api422Error(RuntimeException(error))

                    } else
                        MyException.Api422Error(RuntimeException(message))
                }
            }
            else -> MyException.AutoApiError(e)
        }
    }
}