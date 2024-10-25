package gr.novidea.weatherpay.data

import android.content.Context
import gr.novidea.weatherpay.R


object Constants {
    const val TID: String = "01234567"
    const val MID: String = "0123456789"
    var SN: String = "0123456789"

    const val ADDRESS: String = "Leoforos Eirinis 39, 17778"
    const val PHONE: String = "2101234567"
    const val NAME: String = "Novidea"
    const val CITY: String = "Tauros, Attikis"

    const val GEO: String = "Attica, GR"

    const val ACTIONCODE_CUSTOMER: String = "CustomerReceipt"
    const val ACTIONCODE_MERCHANT: String = "MerchantReceipt"

    var TRANSACTION_TYPES: Array<String> = arrayOf(
        "Purchase", "Refund"
    )

    val TRANSACTION_STATUSES: Array<String> = arrayOf(
        "Settled", "Unsettled"
    )

    class SharedPrefKeys {
        companion object {
            const val RECEIPTS = "Receipts"
            const val MERCHANT = "merchant"
            const val CUSTOMER = "customer"
            const val GREY_LEVEL = "greyLevel"

            const val TRANSACTIONS = "Transactions"
            const val BATCH = "batch"

            const val DEVICE = "Device"
            const val SERIAL_NUMBER = "serialNumber"

            const val LOCALIZATION = "Localization"
            const val LANGUAGE = "language"
            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"
        }
    }

    /**
     * An array of (ButtonItem) items, aka Other Apps.
     */
    fun getOtherItems(context: Context): Array<ButtonItem> {
        return arrayOf(
            ButtonItem(context.getString(R.string.test), R.id.action_otherFragment_to_testFragment, R.drawable.test),
            ButtonItem(
                context.getString(R.string.transaction_list),
                R.id.action_otherFragment_to_transactionsListFragment,
                R.drawable.transaction_list
            )
        )
    }

    fun getSettingsItems(context: Context): Array<ButtonItem> {
        return arrayOf(
            ButtonItem(context.getString(R.string.pos_device_information), R.id.action_settingsFragment_to_POSDeviceInformationFragment),
            ButtonItem(context.getString(R.string.print_management), R.id.action_settingsFragment_to_printerManagementFragment),
            ButtonItem(context.getString(R.string.concluding_message), R.id.action_settingsFragment_to_concludingMessageFragment),
            ButtonItem(context.getString(R.string.language_selection), R.id.action_settingsFragment_to_languageFragment),
            ButtonItem(context.getString(R.string.reboot), -1),
        )
    }

    fun getTestItems(context: Context): Array<ButtonItem> {
        return arrayOf(
            ButtonItem(context.getString(R.string.beep), R.id.action_testFragment_to_beepFragment, R.drawable.beep),
            ButtonItem(context.getString(R.string.print), R.id.action_testFragment_to_printFragment, R.drawable.print),
            ButtonItem(context.getString(R.string.network), R.id.action_testFragment_to_networkFragment, R.drawable.network)
        )
    }

    fun getBeepItems(context: Context): Array<ButtonItem> {
        return arrayOf(
            ButtonItem(context.getString(R.string.beep_number, 1), 10, R.drawable.beep, Sound(0, 1000, false)),
            ButtonItem(context.getString(R.string.beep_number, 2), 11, R.drawable.beep, Sound(1, 1000, false)),
            ButtonItem(context.getString(R.string.beep_number, 3), 12, R.drawable.beep, Sound(2, 1000, false)),
            ButtonItem(context.getString(R.string.beep_number, 4), 13, R.drawable.beep, Sound(3, 1000, false)),
            ButtonItem(context.getString(R.string.beep_number, 5), 14, R.drawable.beep, Sound(4, 1000, false)),
            ButtonItem(context.getString(R.string.beep_number, 6), 15, R.drawable.beep, Sound(5, 1000, false)),
            ButtonItem(context.getString(R.string.multiple_beeps), 16, R.drawable.beep, Sound(-1, 1000, false))
        )
    }

    fun getPrintItems(context: Context): Array<ButtonItem> {
        return arrayOf(
            ButtonItem(context.getString(R.string.text_receipt), 17),
            ButtonItem(context.getString(R.string.multiline_text_receipt), 18),
            ButtonItem(context.getString(R.string.bars_receipt), 19),
            ButtonItem(context.getString(R.string.logo_receipt), 20)
        )
    }

}