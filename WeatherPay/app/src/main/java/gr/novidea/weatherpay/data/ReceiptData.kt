package gr.novidea.weatherpay.data

import android.graphics.Bitmap

data class ReceiptData(
    val reprint: Boolean,
    val tip: Long,
    val logo: Bitmap,
    val weatherIcon: Bitmap,
    val name: String,
    val city: String,
    val address: String,
    val phone: String,
    val value: Long,
    val mid: String,
    val tid: String,
    val number: Int,
    val version: String,
    val batch: Int,
    val sn: String,
    val rrn: String
)