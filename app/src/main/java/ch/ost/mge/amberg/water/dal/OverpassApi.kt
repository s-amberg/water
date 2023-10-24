package ch.ost.mge.amberg.water.dal
import ch.ost.mge.amberg.water.models.OSMResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OverpassApi {

    @GET("api/interpreter")
    @Headers("Accept: application/json")
    fun getOverpass(@Query("data")data: String)
            : Call<OSMResponse>;
}