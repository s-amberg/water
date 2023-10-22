package ch.ost.mge.amberg.water

import ch.ost.mge.amberg.water.models.OSMResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class RestAPI {

    private val api: Api

    init {

        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client : OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openstreetmap.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getPoints(left: String,  bottom: String, right: String, top: String): Call<OSMResponse> {
        return api.getTop("${left},${bottom},${right},${top}")
    }
}