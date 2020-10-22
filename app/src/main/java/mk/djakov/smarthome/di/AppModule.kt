package mk.djakov.smarthome.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.dm7.barcodescanner.core.BuildConfig
import mk.djakov.smarthome.data.repository.MainRepository
import mk.djakov.smarthome.networking.SmartHomeService
import mk.djakov.smarthome.util.Credentials
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
    fun provideMainRepository(
        @DeviceOne deviceOneService: SmartHomeService,
        @DeviceTwo deviceTwoService: SmartHomeService
    ) = MainRepository(deviceOneService, deviceTwoService)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addNetworkInterceptor(interceptor)
            .build()

    @Singleton
    @Provides
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

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
    @DeviceOne
    fun providesDeviceOneSmartHomeService(
        okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): SmartHomeService =
        provideService(
            okHttpClient,
            converterFactory,
            Credentials.BASE_URL_1,
            SmartHomeService::class.java
        )

    @Singleton
    @Provides
    @DeviceTwo
    fun providesDeviceTwoSmartHomeService(
        okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): SmartHomeService =
        provideService(
            okHttpClient,
            converterFactory,
            Credentials.BASE_URL_2,
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