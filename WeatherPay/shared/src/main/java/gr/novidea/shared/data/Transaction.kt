package gr.novidea.shared.data

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class Transaction(
    var id: String = "",
    var icon: String = "",
    var batch: Int = 0,
    var number: Int = 0,
    var date: String = Date().toString(),
    var amount: Long = 0,
    var tip: Long = 0,
    var cardNumber: String = "",
    var cardType: String = "",
    var type: String = "",
    var status: String = "",
    var reader: String = "",
    var total: Long = 0,
) {

    constructor(
        icon: String,
        batch: Int,
        number: Int,
        amount: Long,
        tip: Long,
        cardNumber: String,
        cardType: String,
        type: String,
        status: String,
        reader: String,
        date: String? = null,
        id: String? = null
    ) : this() {
        this.icon = icon
        this.batch = batch
        this.number = number
        this.amount = amount
        this.tip = tip
        this.cardNumber = cardNumber
        this.cardType = cardType
        this.type = type
        this.status = status
        this.reader = reader
        this.date = date ?: Date().toString()
        this.id = id ?: getTransactionId(batch, number)
        this.total = amount + tip
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transaction) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    /**
     * Generates the transaction ID
     * based on the number of the transaction and the batch number
     * @param batch the batch number
     * @param number the transaction number
     *
     * @return the transaction ID
     * example: 001001
     */
    private fun getTransactionId(batch: Int, number: Number): String {
        val batchStr = batch.toString().padStart(3, '0')
        val numberStr = number.toString().padStart(3, '0')
        return "$batchStr$numberStr"
    }
}