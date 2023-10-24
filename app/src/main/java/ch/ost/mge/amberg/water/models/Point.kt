package ch.ost.mge.amberg.water.models

import android.location.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class Point(val lon: Double, val lat: Double) {
    fun kmDistanceAccurate(location: Point): Double {
        //https://www.movable-type.co.uk/scripts/latlong.html

        val earthRadius = 6371e3; // metres
        val lat1 = location.lat * Math.PI/180; // φ, λ in radians
        val lat2 = this.lat * Math.PI/180;
        val deltaLat = (this.lat-location.lat) * Math.PI/180;
        val deltaLon = (this.lon -location.lon) * Math.PI/180;

        val a = sin(deltaLat/2) * sin(deltaLat/2) +
                cos(lat1) * cos(lat2) *
                sin(deltaLon/2) * sin(deltaLon/2);
        val c = 2 * atan2(sqrt(a), sqrt(1-a));

        val d = earthRadius * c; // in metres

        return d / 1000
    }
}

fun Location.toPoint (): Point {return Point(this.longitude, this.latitude)}

val DEFAULT_POINT =  Point(8.8172203, 47.2238426)