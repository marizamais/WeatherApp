package gr.novidea.weatherpay.data

data class Sound(val position: Int, val duration: Int, val sync: Boolean = false) {
    override fun toString(): String = "Sound $position"
}
