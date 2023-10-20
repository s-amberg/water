package ch.ost.mge.amberg.water

import ch.ost.mge.amberg.water.models.OSMResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RestAPI {

    private val api: Api

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openstreetmap.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getPoints(left: String,  bottom: String, right: String, top: String): Call<OSMResponse> {
        return api.getTop("${left},${bottom},${right},${top}")
    }
}