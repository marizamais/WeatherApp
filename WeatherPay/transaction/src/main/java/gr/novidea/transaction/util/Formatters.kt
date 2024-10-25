package gr.novidea.transaction.util

import java.util.Locale

/**
 * Extension function to format a duration in milliseconds to a string
 *
 * @return the formatted duration string
 * ex. 123456 -> "02:03"
 */
fun Long.formatMillis(): String {
    val minutes = (this / 1000) / 60
    val seconds = (this / 1000) % 60
    return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
}
