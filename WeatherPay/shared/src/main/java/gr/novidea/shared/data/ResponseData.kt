package gr.novidea.shared.data

data class ResponseData(
    val uuid: String,
    val createdTime: Long,
    val cardData: CardData,
    val transactionData: TransactionData,
    val merchantData: MerchantData,
    val applicationData: ApplicationData
)