package gr.novidea.weatherpay.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import eu.cpm.connector.utils.receiptbuilder.ReceiptBuilder
import gr.novidea.weatherpay.R

object TestReceiptUtils {
    const val archivoFont = "fonts/Archivo_Condensed-ExtraBold.ttf"
    const val robotoFont = "fonts/roboto_regular.ttf"
    fun textReceipt(
        context: Context
    ): Bitmap {
        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setColor(Color.BLACK).setTextSize(90f)
            .setAlign(Paint.Align.CENTER).setTypeface(context,archivoFont).addText("Text Receipt").addParagraph()
            .setTextSize(60f)
            .setAlign(Paint.Align.LEFT).setTypeface(context, robotoFont).addText("This text is alligned to the left.")
            .setAlign(Paint.Align.RIGHT).addText("This text is alligned to the right.")
            .setAlign(Paint.Align.CENTER).addText("This text is alligned to the center.")
            .setTypeface(context, archivoFont)
            .addParagraph()
            .setAlign(Paint.Align.LEFT).addText("Left Text", false)
            .setAlign(Paint.Align.RIGHT).addText("Right Text")
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()

        return receipt.build()
    }

    fun multiLineReceipt(
        context: Context
    ): Bitmap {
        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setColor(Color.BLACK).setTextSize(90f)
            .setAlign(Paint.Align.CENTER).setTypeface(context,archivoFont).addText("Multiline Text Receipt").addParagraph()
            .setTextSize(80f)
            .setAlign(Paint.Align.LEFT).addText("Sample 1").setAlign(Paint.Align.CENTER)
                .setTypeface(context, robotoFont).addMultilineText(context.getString(R.string.long_text), true)
            .addParagraph()
            .setTypeface(context,archivoFont).setAlign(Paint.Align.LEFT).addText("Sample 2").setAlign(Paint.Align.CENTER)
            .setTypeface(context, robotoFont).addText(context.getString(R.string.long_text_not))
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()

        return receipt.build()
    }

    fun logoReceipt(
        bMap1: Bitmap, bMap2: Bitmap
    ): Bitmap {
        var resized = Bitmap.createScaledBitmap(bMap1, 1000, 300, true)
        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setColor(Color.BLACK).setTextSize(90f).setAlign(Paint.Align.CENTER)
            .addImage(resized).addParagraph().addParagraph()

        resized = Bitmap.createScaledBitmap(bMap2, 500, 500, true)
        receipt.addImage(resized).addParagraph().addParagraph()
            .addParagraph().addParagraph().addParagraph().addParagraph()

        return receipt.build()
    }

    fun barsReceipt(
        bMap: Bitmap
    ): Bitmap {
        val resized = Bitmap.createScaledBitmap(bMap, 1100, 200, true)
        val receipt = ReceiptBuilder(1200)
        receipt.setMargin(0, 10).setColor(Color.BLACK).setTextSize(90f).setAlign(Paint.Align.CENTER)

        // Draw the rectangles
        for (i in 0..3) {
            receipt.addImage(resized).addParagraph().addParagraph()
        }

        receipt.addParagraph().addParagraph().addParagraph().addParagraph()

        // Return the final bitmap
        return receipt.build()
    }

}
