package gr.novidea.shared.data

data class TransactionData(
    val finalCode: String,
    val amount: Long?,
    val redemptionAmount: Int,
    val tip: Long?,
    val totalAmount: Long?,
    val installments: Int,
    val batchNo: Int,
    val sequenceNo: Int,
    val referenceNo: String,
    val authorizationCode: String,
    val bankId: String,
    val cryptogram: String,
    val isReconciled: Boolean,
    val isCancelled: Boolean
)

