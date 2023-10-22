package ch.ost.mge.amberg.water
import ch.ost.mge.amberg.water.models.OSMNode
import ch.ost.mge.amberg.water.models.OSMResponse
import ch.ost.mge.amberg.water.models.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointService(private val api: RestAPI = RestAPI()) {

    private fun getPointsFromAPI(radiusKm: Float, lat: Float, long: Float): Call<OSMResponse> {
        val left = long - kmToLongitude(radiusKm)
        val bottom = lat - kmToLatitude(radiusKm)
        val right = long + kmToLongitude(radiusKm)
        val top = lat + kmToLatitude(radiusKm)

        return api.getPoints(left.toString(), bottom.toString(), right.toString(), top.toString())
    }

    private fun mapOSMReponse(response: OSMResponse): List<OSMNode> {
       return response.elements.filter { it.isWell() }
    }

    fun getPoints(radiusKm: Float = 5F, onSuccess: (points:List<OSMNode>)->Unit, onError: (t:Throwable)->Unit, lat: Float = 8.8F, long: Float = 47.23F): Unit {

            getPointsFromAPI(radiusKm, lat, long).enqueue(object : Callback<OSMResponse> {
                override fun onResponse(call: Call<OSMResponse>, response: Response<OSMResponse>) {
                    val body = response.body();
                    if(body != null) {
                        val points = mapOSMReponse(response.body()!!)
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

fun distance(point1: Point, point2: Point): Double {
    val latDifference = point2.lat - point1.lat
    val longDifference = point2.lon - point1.lon

    return Math.sqrt(Math.pow(latDifference, 2.0) + Math.pow(longDifference, 2.0))
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