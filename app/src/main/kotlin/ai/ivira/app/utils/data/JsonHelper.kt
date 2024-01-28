package ai.ivira.app.utils.data

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.IOException
import java.lang.reflect.ParameterizedType
import javax.inject.Inject

class JsonHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    fun openJsonFromAssets(jsonFile: String): String? {
        return try {
            val inputStream = context.assets.open(jsonFile)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    inline fun <reified T> getList(jsonFile: String): List<T>? {
        val listType = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter: JsonAdapter<List<T>> = getJasonAdapter(listType)
        return adapter.fromJson(jsonFile)
    }

    fun <T> getJasonAdapter(data: ParameterizedType): JsonAdapter<List<T>> {
        return moshi.adapter(data)
    }
}