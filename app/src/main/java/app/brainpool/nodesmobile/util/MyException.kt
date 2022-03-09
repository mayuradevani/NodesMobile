package app.brainpool.nodesmobile.util

sealed class MyException(val throwable: Throwable, val msg: String = "") : Exception() {

    class UnKnownError(throwable: Throwable) : MyException(throwable)
    class AutoApiError(throwable: Throwable) : MyException(throwable)
    class ApiError(throwable: Throwable) : MyException(throwable)
    class Api422Error(throwable: Throwable) : MyException(throwable)
    class NetworkError(throwable: Throwable) : MyException(throwable)
    class IAgreeException(throwable: Throwable):MyException(throwable)
    class UnAuthenticate(throwable: Throwable):MyException(throwable)
    class InvalidCredentials(throwable: Throwable):MyException(throwable)
}