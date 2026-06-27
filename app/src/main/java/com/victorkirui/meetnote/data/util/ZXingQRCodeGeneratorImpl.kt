package com.victorkirui.meetnote.data.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.victorkirui.meetnote.domain.model.SocialQRCodeModel
import com.victorkirui.meetnote.domain.model.WorkQRCodeModel
import com.victorkirui.meetnote.domain.repository.QRCodeGenerator

class ZXingQRCodeGeneratorImpl : QRCodeGenerator {

    // Define standard dimensions for the generated QR codes
    private val qrWidth = 500
    private val qrHeight = 500

    override fun generateWorkQRCode(
        workQRCodeModel: WorkQRCodeModel
    ): ByteArray? {
        // Construct a standard vCard string so phone cameras recognize it as a contact
        val vCardBuilder = StringBuilder().apply {
            append("BEGIN:VCARD\n")
            append("VERSION:3.0\n")
            append("FN:${workQRCodeModel.fullName}\n")
            append("TEL:${workQRCodeModel.phoneNumber}\n")
            append("EMAIL:${workQRCodeModel.email}\n")
            workQRCodeModel.organization?.let { append("ORG:$it\n") }
            workQRCodeModel.role?.let { append("TITLE:$it\n") }

            // Append social links to the note or URL field if present
            workQRCodeModel.socialLinkModel.forEach { link ->
                append("URL:${link.url}\n") // Or format as custom fields depending on your Model structure
            }
            append("END:VCARD")
        }

        return createQRMatrix(vCardBuilder.toString())
    }

    override fun generateSocialQRCode(socialQRCodeModel: SocialQRCodeModel): ByteArray? {
        val socialContentBuilder = StringBuilder().apply {
            append("MeetNote Profile\n")

            // Only append if the field is not null and not blank
            if (socialQRCodeModel.fullName.isNotBlank()) {
                append("Name: ${socialQRCodeModel.fullName}\n")
            }

            if (socialQRCodeModel.phoneNumber?.isNotBlank() == true) {
                append("Phone: ${socialQRCodeModel.phoneNumber}\n")
            }

            if (socialQRCodeModel.email?.isNotBlank() == true) {
                append("Email: ${socialQRCodeModel.email}\n")
            }

            // Filter out any broken or empty social links before iterating
            socialQRCodeModel.socialLinks
                .filter { it.platform.isNotBlank() && it.url.isNotBlank() }
                .forEach { link ->
                    append("${link.platform}: ${link.url}\n")
                }
        }

        // Trim any trailing white spaces/newlines to keep payload minimal
        val optimizedPayload = socialContentBuilder.toString().trim()

        return createQRMatrix(optimizedPayload)
    }

    /**
     * Helper function to handle the ZXing matrix encoding and bitmap conversion
     */
    private fun createQRMatrix(content: String): ByteArray? {
        return try {
            // 1. Generate the BitMatrix using ZXing
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                qrWidth,
                qrHeight
            )

            // 2. Create a temporary Android Bitmap to paint the pixels
            val bitmap = createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
            for (x in 0 until qrWidth) {
                for (y in 0 until qrHeight) {
                    bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            // 3. Compress the Bitmap into a platform-agnostic ByteArray
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}