package ai.ivira.app.utils.ui

fun String.attachListItemToString(list: List<String>): String {
    val newString = this.plus(buildString {
        list.forEach {
            append(",")
            append(it)
        }
    })
    return newString
}