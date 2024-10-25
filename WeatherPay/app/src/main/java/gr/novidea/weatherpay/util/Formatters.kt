package gr.novidea.weatherpay.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension function to round a Double to the specified number of decimal places
 *
 * @param decimalPlaces the number of decimal places to round to
 */
fun Double.roundToDecimalPlaces(decimalPlaces: Int): Double {
    return BigDecimal(this).setScale(decimalPlaces, RoundingMode.HALF_EVEN).toDouble()
}

/**
 * Extension function to mask a credit card number
 *
 * @return the masked credit card number
 * ex. "1234567812345678" -> "1234 5678 **** 5678"
 */
fun String.maskCardNumber(): String {
    // Split crad number into parts of 4 digits
    val parts = this.chunked(4)

    return if (parts.size == 4) {
        "${parts[0]} ${parts[1]} **** ${parts[3]}"
    } else {
        this
    }
}

/**
 * Extension function to format a date string
 *
 * @return the formatted date string
 * ex. "Wed Jul 21 14:00:00 GMT+03:00 2021" -> "21/07/2021 14:00:00"
 */
fun String.formatDate(): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val date: Date = inputFormat.parse(this)!!
    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)

    return outputFormat.format(date)
}
