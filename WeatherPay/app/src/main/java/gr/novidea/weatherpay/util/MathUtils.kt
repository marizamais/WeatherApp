package gr.novidea.weatherpay.util

object MathUtils {

    /**
     * Extension function to clamp a generic comparable value
     * between a minimum and maximum value
     */
    fun <T : Comparable<T>> T.clamp(min: T, max: T): T {
        return when {
            this < min -> min
            this > max -> max
            else -> this
        }
    }

}