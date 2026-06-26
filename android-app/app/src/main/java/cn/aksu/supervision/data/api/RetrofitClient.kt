package cn.aksu.supervision.data.api

import cn.aksu.supervision.data.local.dao.TokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitClient {

    private const val BASE_URL = "https://api.aksu-supervision.cn/"

    /** JWT 认证拦截器 */
    class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()
            val token = tokenProvider()
            val request = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Accept", "application/json")
                    .build()
            } else {
                original.newBuilder()
                    .header("Accept", "application/json")
                    .build()
            }
            return chain.proceed(request)
        }
    }

    /** Token 过期自动刷新拦截器 */
    class TokenRefreshInterceptor(
        private val tokenProvider: () -> String?,
        private val refreshTokenProvider: () -> String?,
        private val onTokenRefreshed: (String, String) -> Unit
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val response = chain.proceed(chain.request())
            if (response.code == 401) {
                response.close()
                val refreshToken = refreshTokenProvider()
                if (!refreshToken.isNullOrBlank()) {
                    // 同步刷新 token（简化实现，生产环境需加锁防并发）
                    synchronized(this) {
                        val refreshRequest = chain.request().newBuilder()
                            .method("POST", okhttp3.FormBody.Builder()
                                .add("refresh_token", refreshToken)
                                .build())
                            .url("${BASE_URL}api/auth/refresh")
                            .build()
                        val refreshResponse = chain.proceed(refreshRequest)
                        if (refreshResponse.isSuccessful) {
                            // 解析新 token 并存储 —— 实际项目中应解析 JSON
                            refreshResponse.close()
                            // 此处简化：返回 401 让上层重新登录
                        }
                    }
                }
            }
            return response
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenDao: TokenDao): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { runBlocking { tokenDao.getAccessToken() } })
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
