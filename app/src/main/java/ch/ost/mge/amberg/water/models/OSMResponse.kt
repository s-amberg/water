package ch.ost.mge.amberg.water.models

class OSMResponse (val elements: List<OSMNode>){

}

class OSMNode(
    val type: String,
    val id: Long,
    lat: Double,
    long: Double,
    val timestamp: String,
    val tags: OSMTag?
): Point(long, lat) {
    fun isWell(): Boolean {
        return type == "node" && tags != null && tags.isWell()
    }

    override fun toString(): String {
        return "$lat $long $tags"
    }

    fun title(): String {
        return if (tags != null && tags.name != null) tags.name else "Unnamed Point"
    }

    fun body(): String {
        return  if(tags != null) tags.description()
                else ""
    }
}

class OSMTag (
    val type: String,
    val amenity: String?,
    val drinking_water: Boolean? = false,
    val name: String?,
    val bottle: Boolean? = false,
    val indoor: Boolean? = false,
    val man_made: String?
    ) {

    override fun toString(): String {
        return name + description()
    }

    fun description(): String {
        return listOfNotNull(
            if (bottle == true) "works with bottles" else null,
            if (drinking_water == true) "drinking water" else null,
            if (man_made == "water_tap") "water tap" else null,
            if (man_made == "water_well") "well" else null,
            if (indoor == true) "indoor" else null
        ).joinToString(", ")
    }

    fun isWell(): Boolean {
        return amenityIsWell() || manMadeIsWell() || drinking_water == true
    }

    private fun amenityIsWell(): Boolean {
        return amenity != null && listOf("drinking_water", "fountain").contains(amenity)
    }
    private fun manMadeIsWell(): Boolean {
        return man_made != null && listOf("water_well", "water_tap").contains(man_made)
    }
}