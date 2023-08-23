package ir.part.app.intelligentassistant.utils.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.part.app.intelligentassistant.features.ava_negar.data.AvanegarService
import retrofit2.Retrofit
import saman.zamani.persiandate.PersianDate
import java.util.Calendar
import java.util.TimeZone

@Module
@InstallIn(SingletonComponent::class)
object DateModule {
    // TODO: improve library. maybe use dandelion
    @Provides
    fun providePersianDate() = PersianDate()

    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))

}