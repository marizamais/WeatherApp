package gr.novidea.transaction.data

import eu.cpm.connector.base.transactionparameter.EReaderType


object Constants {
    var READERS: String = EReaderType.MAG_ICC_PICC.toString()
    const val TID: String = "01234567"
    const val MID: String = "E000000101"
    var SN: String = "0123456789"

    const val ADDRESS: String = "Leoforos Eirinis 39, 17778"
    const val PHONE: String = "2101234567"
    const val NAME: String = "Novidea"
    const val CITY: String = "Tauros, Attikis"
    const val POSTCODE: String = "123 45"
    const val COUNTRY: String = "Greece"

    class EmvTags {
        companion object {
            const val APP_LABEL = "0x04"
            const val CARD_NUMBER_MASKED = "0x319"

        }
    }

    class TransactionPinStates {
        companion object {
            const val PIN_ENTRY = "PIN_ENTRY"
            const val PIN_OK = "PIN_OK"
            const val PIN_TRIES_LEFT = "PIN_TRIES_LEFT"
            const val PIN_STAR_MESSAGE = "PIN_STAR_MESSAGE"
            const val PIN_VERIFY_FAILED = "PIN_VERIFY_FAILED"
            const val PIN_CANCELED = "PIN_CANCELLED"
        }
    }

    data class Setup(val message: String, val icons: Array<Int>)
    class ReadersSetup {
        companion object {
            private val setups = mapOf(
                EReaderType.MAG_ICC_PICC.toString() to Setup(
                    "Insert, Swipe or Tap to pay", arrayOf(0, 1, 2)
                ),
                EReaderType.MAG_ICC.toString() to Setup("Insert or Swipe to pay", arrayOf(0, 1)),
                EReaderType.MAG.toString() to Setup("Swipe to pay", arrayOf(1))
            )

            fun getSetup(key: String): Setup? {
                return setups[key]
            }
        }
    }

}