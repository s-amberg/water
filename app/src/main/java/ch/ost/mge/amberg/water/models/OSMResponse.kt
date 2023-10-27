package ch.ost.mge.amberg.water.models

class OSMResponse (val elements: List<OSMRawNode>){

}
class OSMRawNode(
    val type: String,
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val center: Point?,
    val tags: OSMRawTag?
){
    // if type is not node(way/relation), its lon/lat is an average of all nodes in Node (center)
    fun toOSMNode(location: Point): OSMNode? {
        val lat: Double? = if(type == "node" && lat != null) lat else center?.lat
        val lon: Double? = if(type == "node" && lon != null) lon else center?.lon

        return if (lat != null && lon != null) OSMNode(type, id, lat, lon, tags?.toOSMTag(), location) else null
    }
}
class OSMRawTag(    val type: String?,
                    val amenity: String?,
                    val fee: String?,
                    val drinking_water: String?,
                    val name: String?,
                    val bottle: String?,
                    val indoor: String?,
                    val man_made: String?,
                    val natural: String?,
                    val fountain: String?,
                    val access: String?,
                    ) {
    private fun isYes(strBoolean: String?): Boolean? {
        return  if(strBoolean != null) strBoolean == "yes"
        else null
    }
    fun toOSMTag(): OSMTag {
        return OSMTag(
            type, amenity, fee, isYes(drinking_water), name, isYes(bottle), isYes(indoor), man_made, natural, fountain, isYes(access)
        )
    }
}

class OSMNode(
    val type: String,
    val id: Long,
    lat: Double,
    lon: Double,
    val tags: OSMTag?,
    location: Point
): Point(lon, lat) {

    val distance: Double = kmDistanceAccurate(location)

    fun isWell(): Boolean {
        return type == "node" && tags != null && tags.isWell()
    }

    override fun toString(): String {
        return "$lat $lon ${title()} ${body()}"
    }

    fun title(): String {
        return tags?.title() ?: "Unnamed Point"
    }

    fun body(): String {
        return  if(tags != null) tags.description()
                else ""
    }
}


class OSMTag (
    val type: String?,
    val amenity: String?,
    val fee: String?,
    val drinking_water: Boolean? = false,
    val name: String?,
    val bottle: Boolean? = false,
    val indoor: Boolean? = false,
    val man_made: String?,
    val natural: String?,
    val fountain: String?,
    val access: Boolean?,
    ) {

    override fun toString(): String {
        return name + description()
    }

    fun title(): String? {
        return name ?:
        if (fountain!=null) "fountain"
        else if (amenity == "toilets") amenity
        else manMadeTitle()
    }

    fun description(): String {
        return listOfNotNull(
            if (bottle == true) "works with bottles" else null,
            if (drinking_water == true) "drinking water" else null,
            if (amenity == "toilets") "toilets ${fee ?: ""}" else null,
            if (title() != man_made) manMadeTitle() else null,
            if (indoor == true) "indoor" else null,
            if (natural == "spring") "natural spring" else null,
            if (fountain != null) "fountain: $fountain" else null,
            if (access == true) "accessible" else null
        ).joinToString(", ")
    }

    fun isWell(): Boolean {
        return amenityIsWell() || ( manMadeIsWell() && drinking_water == true )
    }

    private fun amenityIsWell(): Boolean {
        return amenity != null && listOf("drinking_water", "fountain").contains(amenity)
    }
    private fun manMadeIsWell(): Boolean {
        return man_made != null && listOf("water_well", "water_tap").contains(man_made)
    }
    private fun manMadeTitle(): String? {
        return if (man_made == "water_tap") "water tap" else if (man_made == "water_well") "well" else null
    }
}