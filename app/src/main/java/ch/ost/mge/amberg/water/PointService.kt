package ch.ost.mge.amberg.water
import ch.ost.mge.amberg.water.models.OSMNode
import ch.ost.mge.amberg.water.models.Point
import io.reactivex.rxjava3.core.Observable

class PointService(private val api: RestAPI = RestAPI()) {

    fun getPoints(radiusKm: Float = 5F, lat: Float = 11.6F, long: Float = 46.6F): Observable<List<OSMNode>> {


        val left = long - kmToLongitude(radiusKm)
        val bottom = lat - kmToLatitude(radiusKm)
        val right = long + kmToLongitude(radiusKm)
        val top = lat + kmToLatitude(radiusKm)


        return Observable.create { emitter ->

            val response = api.getPoints(left.toString(), bottom.toString(), right.toString(), top.toString()).execute()

            if(response.isSuccessful && response.body()?.elements != null) {

                val points = response.body()!!.elements.filter { it.isWell() }
                emitter.onNext(points)
                emitter.onComplete()
            }
            else emitter.onError(Throwable(response.message()))
        }
    }



}

fun distance(point1: Point, point2: Point): Double {
    val latDifference = point2.lat - point1.lat
    val longDifference = point2.long - point1.long

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