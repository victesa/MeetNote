package com.victorkirui.meetnote.domain.util.qr

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ScanResultManager {
    private val _pendingResult = MutableStateFlow<String?>(null)
    val pendingResult = _pendingResult.asStateFlow()

    fun setResult(result: String) {
        _pendingResult.value = result
    }

    fun clear() {
        _pendingResult.value = null
    }
}
