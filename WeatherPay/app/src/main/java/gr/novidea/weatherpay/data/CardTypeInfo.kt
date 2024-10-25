package gr.novidea.weatherpay.data

data class CardTypeInfo(
    var type: String,
    var num: Int,
    var purchase: Int,
    var tip: Int,
    var cancel: Int,
    var refund: Int,
    var purchaseAmount: Long,
    var cancelAmount: Long,
    var refundAmount: Long,
    var tipAmount: Long,
    var amount: Long
)