package com.example.offlinetranslator

import java.util.Locale
import kotlin.math.sqrt

object AppUtils {
    fun calculateRms(buffer: ByteArray, length: Int): Double {
        var sum = 0.0
        val sampleCount = length / 2
        if (sampleCount == 0) {
            return 0.0
        }
        var i = 0
        while (i < sampleCount) {
            val index = i * 2
            val low = buffer[index].toInt() and 0xFF
            val high = buffer[index + 1].toInt()
            val sample = (high shl 8) or low
            val value = if (sample > 32767) sample - 65536 else sample
            sum += value.toDouble() * value.toDouble()
            i += 1
        }
        return sqrt(sum / sampleCount)
    }

    fun formatStorageSize(bytes: Long): String {
        if (bytes <= 0) {
            return "0 MB"
        }
        val mb = bytes / (1024.0 * 1024.0)
        return String.format(Locale.US, "%.1f MB", mb)
    }

    fun escapeCsv(text: String): String {
        val escaped = text.replace(""", """")
        return ""$escaped""
    }
}
