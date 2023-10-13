package ch.ost.mge.amberg.water
import ch.ost.mge.amberg.water.models.Point
import io.reactivex.rxjava3.core.Observable

class PointService {

    fun getPoints(): Observable<List<Point>> {

        val points = (1..20).map { e -> Point(e, e*2) }

        return Observable.create { emitter ->
            emitter.onNext(points)
            emitter.onComplete()
        }
    }

}