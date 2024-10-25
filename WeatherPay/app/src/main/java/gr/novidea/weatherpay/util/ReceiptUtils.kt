package gr.novidea.weatherpay.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import eu.cpm.connector.utils.receiptbuilder.ReceiptBuilder
import gr.novidea.shared.utils.CurrencyUtils
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.MessagesViewModel
import gr.novidea.weatherpay.data.ReceiptData
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume


object ReceiptUtils {
    suspend fun getCustomerReceipt(context: Context, data: ReceiptData, lifecycleScope: LifecycleCoroutineScope, viewModel: MessagesViewModel
    ): Bitmap {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val resized = Bitmap.createScaledBitmap(data.logo, 1100, 300, true)
        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setColor(Color.BLACK).setTextSize(70f).setAlign(Paint.Align.CENTER)
        if (data.reprint) {
            receipt.setTextSize(80F).addText(context.getString(R.string.reprint_receipt)).addParagraph()
        }
        receipt.setTextSize(70F).addImage(resized).addParagraph().addText(data.name)
            .addText(data.city).addText(data.address).addText("TEL: ${data.phone}").addParagraph()
            .setTextSize(150F).addText("-----------------------------").setTextSize(70F)
        receipt.addText("${date} ${time}")
        receipt.addText(context.getString(R.string.purchase)).setTextSize(150F).addText("-----------------------------")
            .setTextSize(70F).addParagraph().setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.amount), false).setAlign(Paint.Align.RIGHT)
            .addText(CurrencyUtils.longToCurrency(data.value, CurrencyUtils.getCurrencyLocales()))
        if (data.tip.toInt() != 0) {
            receipt.setAlign(Paint.Align.LEFT).addText(context.getString(R.string.tip), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(data.tip, CurrencyUtils.getCurrencyLocales())).setTextSize(150F)
                .addText("--------").setAlign(Paint.Align.LEFT).setTextSize(70F)
                .addText(context.getString(R.string.total), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(data.value + data.tip, CurrencyUtils.getCurrencyLocales())).setTextSize(70F)
        }
        receipt.addParagraph().setAlign(Paint.Align.CENTER).setTextSize(150F)
            .addText("-----------------------------").setTextSize(70F).setAlign(Paint.Align.LEFT)
            .addText("TID: ${data.tid}").addText("VER: ${data.version} RRN: ${data.rrn}")
            .addParagraph().addParagraph().setAlign(Paint.Align.CENTER)

        val resizedIcon = Bitmap.createScaledBitmap(
            toBlackAndWhite(data.weatherIcon), 500, 500, true
        )
        receipt.addImage(resizedIcon)

        return suspendCancellableCoroutine { continuation ->
            lifecycleScope.launch {
                val list = viewModel.getCheckedMessages()
                Log.v("dsf", list.size.toString())
                for (x in list) {
                    if (x.checked) {
                        Log.v("dsf", x.string)
                        receipt.addText(x.string)
                    }
                }
                receipt.addParagraph().addParagraph().addParagraph().addText(context.getString(R.string.customer_copy))
                    .addParagraph().addParagraph().addParagraph().addParagraph().addParagraph()
                    .addParagraph()
                continuation.resume(receipt.build())
            }
        }
    }

    fun getProducerReceipt(context: Context, data: ReceiptData): Bitmap {
        val value = "02606700000100"
        val barcodeBitmap = generateBarcode(value)

        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val resized = Bitmap.createScaledBitmap(data.logo, 1100, 300, true)
        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setColor(Color.BLACK).setTextSize(70f).setAlign(Paint.Align.CENTER)
        if (data.reprint) {
            receipt.setTextSize(80F).addText(context.getString(R.string.reprint_receipt)).addParagraph()
        }
        receipt.setTextSize(70F).addImage(resized).addParagraph().addText(data.name)
            .addText(data.city).addText(data.address).addText("TEL: ${data.phone}").addParagraph()
            .setTextSize(150F).addText("-----------------------------").setTextSize(70F)
        receipt.addText("${date} ${time} ${data.number}")
        receipt.addText(context.getString(R.string.purchase)).setTextSize(150F).addText("-----------------------------")
            .setTextSize(70F).addParagraph().setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.amount), false).setAlign(Paint.Align.RIGHT)
            .addText(CurrencyUtils.longToCurrency(data.value, CurrencyUtils.getCurrencyLocales()))
        if (data.tip.toInt() != 0) {
            receipt.setAlign(Paint.Align.LEFT).addText(context.getString(R.string.tip), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(data.tip, CurrencyUtils.getCurrencyLocales())).setTextSize(150F)
                .addText("--------").setAlign(Paint.Align.LEFT).setTextSize(70F)
                .addText(context.getString(R.string.total), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(data.value + data.tip, CurrencyUtils.getCurrencyLocales())).setTextSize(70F)
        }
        receipt.addParagraph().setAlign(Paint.Align.CENTER).setTextSize(150F)
            .addText("-----------------------------").setTextSize(70F).setAlign(Paint.Align.LEFT)
            .addText("MID: ${data.mid} - TID: ${data.tid}")
            .addText("BATCH: ${data.batch} - SEQ: ${data.number} - RRN: ${data.rrn}")
            .addText("VER: ${data.version} SN: ${data.sn}").setAlign(Paint.Align.CENTER)
            .addParagraph().addParagraph().addParagraph().addImage(barcodeBitmap).addParagraph()
            .addText(value).addParagraph().addText(context.getString(R.string.merchant_copy)).addParagraph()
            .addParagraph().addParagraph().addParagraph().addParagraph().addParagraph()

        return receipt.build()
    }

    private fun generateBarcode(content: String): Bitmap {
        val width = 1100
        val height = 300
        val bitMatrix: BitMatrix =
            MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    fun toBlackAndWhite(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blackAndWhiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
                val newPixel = if (gray > 128) Color.WHITE else Color.BLACK
                blackAndWhiteBitmap.setPixel(x, y, newPixel)
            }
        }
        return blackAndWhiteBitmap
    }

}