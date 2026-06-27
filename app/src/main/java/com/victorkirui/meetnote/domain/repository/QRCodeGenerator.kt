package com.victorkirui.meetnote.domain.repository

import com.victorkirui.meetnote.domain.model.SocialQRCodeModel
import com.victorkirui.meetnote.domain.model.WorkQRCodeModel

interface QRCodeGenerator {
    fun generateWorkQRCode(workQRCodeModel: WorkQRCodeModel): ByteArray?

    fun generateSocialQRCode(socialQRCodeModel: SocialQRCodeModel): ByteArray?
}
