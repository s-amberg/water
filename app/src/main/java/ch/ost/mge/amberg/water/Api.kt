package ch.ost.mge.amberg.water
import ch.ost.mge.amberg.water.models.OSMResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("/api/0.6/map") //?bbox=left,bottom,right,top
    fun getTop(@Query("bbox") after: String)
            : Call<OSMResponse>;
}