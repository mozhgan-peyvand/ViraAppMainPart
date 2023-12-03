package ai.ivira.app.utils.data.exceptions

class RemoteDataSourceException(
    val code: Int,
    val body: String
) : Exception("Remote error: code=$code, body: ${prepareBody(body)}")

private fun prepareBody(body: String): String {
    return body.replace("(\\r\\n)|\\n".toRegex(), " ")
        .replace("\\s".toRegex(), " ")
}