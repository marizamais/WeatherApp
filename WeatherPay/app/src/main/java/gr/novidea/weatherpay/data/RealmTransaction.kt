package gr.novidea.weatherpay.data

import gr.novidea.shared.data.Transaction
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
open class RealmTransaction() : RealmObject {

    @PrimaryKey
    var id: String = ""
    var batch: Int = 0
    var number: Int = 0
    var date: String = Date().toString()
    var amount: Long = 0
    var tip: Long = 0
    var cardNumber: String = ""
    var cardType: String = ""
    var type: String = ""
    var status: String = ""
    var reader: String = ""
    var total: Long = 0

    constructor(transaction: Transaction) : this() {
        this.id = transaction.id
        this.batch = transaction.batch
        this.number = transaction.number
        this.date = transaction.date
        this.amount = transaction.amount
        this.tip = transaction.tip
        this.cardNumber = transaction.cardNumber
        this.cardType = transaction.cardType
        this.type = transaction.type
        this.status = transaction.status
        this.reader = transaction.reader
        this.total = transaction.total
    }
}