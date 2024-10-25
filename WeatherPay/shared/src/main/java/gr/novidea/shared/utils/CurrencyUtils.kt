package gr.novidea.shared.utils

import eu.cpm.connector.utils.currencies.parser.CurrencyParse

object CurrencyUtils {


    /**
     * Returns the currency locales as needed based on the device's locale.
     *
     * @return The currency locales.
     */
    fun getCurrencyLocales(): CurrencyParse.CurrencyLocales {

        return CurrencyParse.CurrencyLocales.GREECE
    }


    /**
     * Converts a long value to a monetary amount string.
     *
     * @param amount The long value to convert.
     * @param currencyLocales The currency locales to use for the conversion.
     * @return The monetary amount string.
     */
    fun longToCurrency(amount: Long, currencyLocales: CurrencyParse.CurrencyLocales): String {
        return CurrencyParse.parseLongAmountToString(
            amount, currencyLocales, CurrencyParse.CurrencyParseSymbol.LEFT, true
        )
    }


    /**
     * Converts a monetary amount string to a long value.
     *
     * @param amount The amount string to convert. Example: "€ 1,234.56"
     * @return The long value of the amount.
     */
    fun currencyToLong(amount: String): Long {
        var value = amount
        if (amount.replace(Regex("[.,€]"), "").length > 11) {
            value  = amount.replace(Regex("[.,€]"), "").substring(0, 12)
        }
        return CurrencyParse.parseStringAmountToLong(
            value, CurrencyParse.CurrencyParseSymbol.LEFT, true
        )
    }
}