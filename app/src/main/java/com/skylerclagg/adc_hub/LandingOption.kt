enum class LandingOption(val points: Int, val displayName: String) {
    NONE(0, "None"),
    SMALL_CUBE(25, "Small Cube"),
    LARGE_CUBE(15, "Large Cube"),
    LANDING_PAD(15, "Landing Pad"),
    BULLSEYE(25, "Bullseye");

    override fun toString(): String = displayName
}
