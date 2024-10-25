package gr.novidea.weatherpay.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import eu.cpm.connector.utils.receiptbuilder.ReceiptBuilder
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.BatchReceiptData
import gr.novidea.weatherpay.data.CardTypeInfo
import gr.novidea.weatherpay.data.Constants
import gr.novidea.shared.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BatchReceiptUtils {

    private const val archivoFont = "fonts/Archivo_Condensed-ExtraBold.ttf"
    private const val robotoFont = "fonts/roboto_regular.ttf"
    fun getBatchReceipt(data: BatchReceiptData, context: Context): Bitmap {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val locale = CurrencyUtils.getCurrencyLocales()

        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setTypeface(context, archivoFont).setColor(Color.BLACK)
            .setTextSize(75f).setAlign(Paint.Align.CENTER).addText("DEMO TEST CARDLINK")
            .addText(Constants.ADDRESS).addText("Merchant Country").addParagraph()
            .setTypeface(context, robotoFont).addText("$date $time").addParagraph()
            .addText("MID: ${Constants.MID} - TID: ${Constants.TID}").addParagraph()
            .setTypeface(context, archivoFont).addText("BATCH: ${data.transactionsList[0].batch}")
            .addParagraph().setTypeface(context, robotoFont)

        val cardTypes: MutableList<CardTypeInfo> = mutableListOf()
        var tipNumber = 0
        var purchase = 0
        var cancel = 0
        var refund = 0
        for (transaction in data.transactionsList) {
            val amount = CurrencyUtils.longToCurrency(transaction.amount, locale)
            val tip = CurrencyUtils.longToCurrency(transaction.tip, locale)
            Log.v("ok", transaction.cardType)
            if (!cardTypes.any { it.type == transaction.cardType }) {
                cardTypes.add(CardTypeInfo(transaction.cardType, 0, 0, 0, 0, 0,0,0,0,0,0))
            }

            val item = cardTypes.find { it.type == transaction.cardType }
            if (item != null) {
                item.num += 1
                item.amount += transaction.amount
            }

            if (transaction.type == "Purchase") {
                purchase += 1
                if (item != null) {
                    item.purchase += 1
                    item.purchaseAmount += transaction.amount
                }
                if (tip != context.getString(R.string.zero_euro)) {
                    tipNumber += 1
                    if (item != null) {
                        item.tip += 1
                        item.tipAmount += transaction.tip
                    }
                }
            }

            if (transaction.type == "Cancellation") {
                cancel += 1
                if (item != null) {
                    item.cancel += 1
                    item.cancelAmount += transaction.amount
                }
            }
            if (transaction.type == "Refund") {
                refund += 1
                if (item != null) {
                    item.refund += 1
                    item.refundAmount += transaction.amount
                }
            }

            receipt.setAlign(Paint.Align.LEFT).addText(transaction.id, false)
                .setAlign(Paint.Align.RIGHT).addText(transaction.date.formatDate())
                .setAlign(Paint.Align.LEFT).addText(transaction.type, false)
                .setAlign(Paint.Align.RIGHT).addText(amount)

            if (tip != context.getString(R.string.zero_euro)) {
                receipt.setAlign(Paint.Align.LEFT).addText(context.getString(R.string.tip), false)
                    .setAlign(Paint.Align.RIGHT).addText(tip)
            }
                receipt.setTextSize(55f)
            val types = transaction.cardType.split(" ")
            val type = if (types.size > 1) {
                types[1]
            } else {
                types[0]
            }
            receipt.setAlign(Paint.Align.LEFT).addText(type, false)
                .setAlign(Paint.Align.RIGHT).addText(transaction.cardNumber).setTextSize(75f)
                .setAlign(Paint.Align.LEFT).addText(transaction.reader, true)
                .addParagraph()
        }

        val totalTransactions = data.transactionsList.size.toString()
        val totalAmountStr = CurrencyUtils.longToCurrency(data.totalAmount, locale)
        val totalTipStr = CurrencyUtils.longToCurrency(data.totalTip, locale)
        val totalStr = CurrencyUtils.longToCurrency(data.total, locale)

        receipt.setTextSize(150F).addText("-----------------------------").setTextSize(80F)
            .addParagraph().setAlign(Paint.Align.CENTER).setTypeface(context, archivoFont)
            .addText("TOTAL:").setTextSize(150F).addText("-----------------------------")
            .setTextSize(75F).setTypeface(context, archivoFont).setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.total_transaction), false)
            .setAlign(Paint.Align.RIGHT).addText(totalTransactions).setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.total_amount), false).setAlign(Paint.Align.RIGHT)
            .addText(totalAmountStr).setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.total_tip_amount), false)
            .setAlign(Paint.Align.RIGHT).addText(totalTipStr).setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.total), false).setAlign(Paint.Align.RIGHT)
            .addText(totalStr).addParagraph().setTypeface(context, robotoFont)
            .setAlign(Paint.Align.LEFT).addText(context.getString(R.string.purchase), false)
            .setAlign(Paint.Align.CENTER).addText(purchase.toString(), false).setAlign(Paint.Align.RIGHT)
            .addText(totalAmountStr).setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.tip), false).setAlign(Paint.Align.CENTER)
            .addText(tipNumber.toString(), false).setAlign(Paint.Align.RIGHT).addText(totalTipStr)
            .setAlign(Paint.Align.LEFT).addText(context.getString(R.string.cancellation), false)
            .setAlign(Paint.Align.CENTER).addText(cancel.toString(), false).setAlign(Paint.Align.RIGHT)
            .addText(context.getString(R.string.zero_euro)).setAlign(Paint.Align.LEFT)
            .addText(context.getString(R.string.refund), false).setAlign(Paint.Align.CENTER)
            .addText(refund.toString(), false).setAlign(Paint.Align.RIGHT)
            .addText(context.getString(R.string.zero_euro)).addParagraph()

        for (card in cardTypes) {
            receipt.setAlign(Paint.Align.CENTER).setTypeface(context, archivoFont)
                .addText(card.type.split(" ").reversed().joinToString(" ").uppercase())
                .setTypeface(context, robotoFont).setAlign(Paint.Align.LEFT)
                .addText(context.getString(R.string.total_transaction), false)
                .setAlign(Paint.Align.RIGHT).addText(card.num.toString())
                .setAlign(Paint.Align.LEFT).addText(context.getString(R.string.total_amount), false)
                .setAlign(Paint.Align.RIGHT).addText(CurrencyUtils.longToCurrency(card.amount, locale)).addParagraph()
                .setAlign(Paint.Align.LEFT).addText(context.getString(R.string.purchase), false)
                .setAlign(Paint.Align.CENTER).addText(card.purchase.toString(), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(card.purchaseAmount, locale)).setAlign(Paint.Align.LEFT)
                .addText(context.getString(R.string.tip), false).setAlign(Paint.Align.CENTER)
                .addText(card.tip.toString(), false).setAlign(Paint.Align.RIGHT).addText(
                    CurrencyUtils.longToCurrency(card.tipAmount, locale))
                .setAlign(Paint.Align.LEFT).addText(context.getString(R.string.cancellation), false)
                .setAlign(Paint.Align.CENTER).addText(card.cancel.toString(), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(card.cancelAmount, locale)).setAlign(Paint.Align.LEFT)
                .addText(context.getString(R.string.refund), false).setAlign(Paint.Align.CENTER)
                .addText(card.refund.toString(), false).setAlign(Paint.Align.RIGHT)
                .addText(CurrencyUtils.longToCurrency(card.refundAmount, locale)).addParagraph().addParagraph()
        }

        receipt.setAlign(Paint.Align.CENTER).addText("DETAILED BATCH RECEIPT").addParagraph()
            .addParagraph().addParagraph().addParagraph().addParagraph().addParagraph()

        return receipt.build()
    }
}
