package gr.novidea.shared.data

data class IntentTransaction(
    val id: Long,
    val amount: Long,
    val tip: Long,
    val installments: Int
)
