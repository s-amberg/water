package ch.ost.mge.amberg.water.dal

import ch.ost.mge.amberg.water.models.OSMResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RestAPI {

    private val osmApi: Api
    private val overpassApi: OverpassApi

    init {

        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client : OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

        val OSMRetrofit = Retrofit.Builder()
            .baseUrl("https://api.openstreetmap.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
        osmApi = OSMRetrofit.create(Api::class.java)


        val OverpassRetrofit = Retrofit.Builder()
            .baseUrl("https://overpass.osm.ch/api/interpreter/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
       overpassApi = OverpassRetrofit.create(OverpassApi::class.java)
    }
    private fun boundingBox(left: String,  bottom: String, right: String, top: String): String {
        return "${left},${bottom},${right},${top}"
    }


    fun getPoints(left: String,  bottom: String, right: String, top: String): Call<OSMResponse> {
        return osmApi.getTop(boundingBox(left, bottom, right, top))
    }

    fun getOverpassPoints(left: String,  bottom: String, right: String, top: String): Call<OSMResponse> {

        fun withBoundingBox(query: String, boundingBox: String): String {
            return "$query($boundingBox);"
        }
        fun tagQuery(elementType: String, key: String, value: String): String {
            return "$elementType[\"$key\"=\"$value\"]"
        }
        fun query(left: String,  bottom: String, right: String, top: String, queries: List<(String)>): String {
            val boundingBox = boundingBox(left, bottom , right, top)

            return "[out:json];" +
                    "(" +
                    queries.joinToString(separator = "") { withBoundingBox(it, boundingBox) } +
                    ");" +
                    "out center;"

        }
        val queries = listOf(
            tagQuery("nwr", "drinking_water", "yes"),
            tagQuery("nwr", "amenity", "drinking_water"),
            tagQuery("nwr", "man_made", "water_tap"),
            tagQuery("nwr", "man_made", "water_well"),
            tagQuery("node", "natural", "spring"),
        )

        return overpassApi.getOverpass(query(left, bottom, right, top, queries))
    }
}