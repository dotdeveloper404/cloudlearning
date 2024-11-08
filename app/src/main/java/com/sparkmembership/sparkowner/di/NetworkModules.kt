package com.sparkmembership.sparkowner.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sparkmembership.sparkowner.BuildConfig
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.APP_BASEURL
import com.sparkmembership.sparkowner.constant.NETWORK_REQUEST_TIMEOUT
import com.sparkmembership.sparkowner.constant.OS_ANDROID
import com.sparkmembership.sparkowner.data.remote.APIService
import com.sparkmembership.sparkowner.data.remote.AuthAPIService
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModules {

    @Provides
    @Singleton
    fun providesDevUrl(): String = APP_BASEURL

    @Provides
    @Singleton
    fun providesHttpLogger(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )

    @Provides
    fun providesGson(): Gson = GsonBuilder().create()

    @Provides
    fun providesGsonFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun providesNetworkInterceptor(): Request {
        return Request.Builder()
            .header("Content-Type","application/json")
            .header("platform", OS_ANDROID)
            .header("app-version", BuildConfig.BUILD_TYPE)
            .build()
    }

    @Provides
    fun providesNetworkingTimeout(): Long = NETWORK_REQUEST_TIMEOUT


    @Provides
    @Singleton
    fun providesOkHttpClient(
        timeOutInSeconds: Long, interceptor:
        HttpLoggingInterceptor,
        localRepository: LocalRepository,
        appConfig: AppConfig
    ): OkHttpClient {
        return OkHttpClient.Builder()
//            .authenticator(TokenAuthenticator(localRepository, appConfig))
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer ${appConfig.AUTH_TOKEN}")
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)

            }
            .addInterceptor(interceptor)
            .build()
    }


    @Provides
    @Singleton
    fun providesRetrofit(
        baseUrl: String, gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthAPIService(retrofit: Retrofit): AuthAPIService =
        retrofit.create(AuthAPIService::class.java)
}

class AuthInterceptor @Inject constructor(appConfig: AppConfig
) : Interceptor {
    private val authToken=appConfig.AUTH_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "Bearer $authToken")
        request.addHeader("Content-Type", "application/json")
        return chain.proceed(request.build())
    }
}
