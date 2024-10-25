package gr.novidea.weatherpay.data

data class BatchReceiptData(
    val transactionsList: List<RealmTransaction>,
    val totalAmount: Long,
    val totalTip: Long,
    val total: Long
)
