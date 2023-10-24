package ch.ost.mge.amberg.water
import ch.ost.mge.amberg.water.dal.RestAPI
import ch.ost.mge.amberg.water.models.DEFAULT_POINT
import ch.ost.mge.amberg.water.models.OSMNode
import ch.ost.mge.amberg.water.models.OSMResponse
import ch.ost.mge.amberg.water.models.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.pow
import kotlin.math.sqrt

class PointService(private val api: RestAPI = RestAPI()) {

    private fun getPointsFromAPI(radiusKm: Float, useOverpass: Boolean, location: Point): Call<OSMResponse> {
        val left = location.lat - kmToLatitude(radiusKm)
        val bottom = location.lon - kmToLongitude(radiusKm)
        val right = location.lat + kmToLatitude(radiusKm)
        val top = location.lon + kmToLongitude(radiusKm)

        return  if(useOverpass) api.getOverpassPoints(left.toString(), bottom.toString(), right.toString(), top.toString())
                else api.getPoints(left.toString(), bottom.toString(), right.toString(), top.toString())
    }

    private fun mapOSMResponse(response: OSMResponse, location: Point): List<OSMNode> {
       return response.elements.mapNotNull { it.toOSMNode(location) }.filter { it.isWell() }
    }

    fun getPoints(
        radiusKm: Float = 5F,
        location: Point,
        useOverpass: Boolean,
        onSuccess: (points:List<OSMNode>)->Unit,
        onError: (t:Throwable)->Unit): Unit {

        getPointsFromAPI(radiusKm, useOverpass, location).enqueue(object : Callback<OSMResponse> {
            override fun onResponse(call: Call<OSMResponse>, response: Response<OSMResponse>) {
                val body = response.body();
                if(body != null) {
                    val points = mapOSMResponse(body, location).sortedBy { it.distance }
                    onSuccess(points)
                }
                else onError(Throwable(response.message()))
            }
            override fun onFailure(call: Call<OSMResponse>, t: Throwable) {
                onError(t)
            }
        })
    }



}

fun kmToLongitude(kms: Float): Float {
    //this is only an approximation
    val oneKmInLongitude: Float = 1/113F
    return kms * oneKmInLongitude
}
fun kmToLatitude(kms: Float): Float {
    //this is only an approximation
    val oneKmInLatitude: Float = 1/106F
    return kms * oneKmInLatitude
}