package mk.djakov.smarthome.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import mk.djakov.smarthome.BuildConfig
import mk.djakov.smarthome.data.repository.MainRepository
import mk.djakov.smarthome.db.AppDatabase
import mk.djakov.smarthome.db.DeviceDao
import mk.djakov.smarthome.networking.SmartHomeService
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.MIGRATION_5_6
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSmartHomeDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "smart_home_db")
        .addMigrations(MIGRATION_5_6)
        .build()

    @Singleton
    @Provides
    fun provideDeviceDao(db: AppDatabase) = db.deviceDao()

    @Singleton
    @Provides
    fun provideMainRepository(
        smartHomeService: SmartHomeService,
        deviceDao: DeviceDao
    ) = MainRepository(smartHomeService, deviceDao)

    @Singleton
    @Provides
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Singleton
    @Provides
    fun providesSmartHomeService(
        okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): SmartHomeService =
        provideService(
            okHttpClient,
            converterFactory,
            Const.BASE_URL,
            SmartHomeService::class.java
        )

    private fun <T> provideService(
        okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory,
        baseUrl: String,
        clazz: Class<T>
    ): T {
        return createRetrofit(okHttpClient, converterFactory, baseUrl).create(clazz)
    }

    private fun createRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }
}