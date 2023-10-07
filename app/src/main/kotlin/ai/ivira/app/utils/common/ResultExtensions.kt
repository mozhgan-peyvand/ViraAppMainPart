package ai.ivira.app.utils.common

fun <T> Result<T>.ifFailure(block: (Throwable?) -> Unit): Result<T> {
    if (isFailure) {
        block(exceptionOrNull())
    }
    return this
}