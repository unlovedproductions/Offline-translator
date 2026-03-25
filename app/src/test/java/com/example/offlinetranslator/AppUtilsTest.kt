package com.example.offlinetranslator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUtilsTest {

    @Test
    fun calculateRms_returnsExpectedValue() {
        val sample = 10000
        val buffer = ByteArray(4)
        buffer[0] = (sample and 0xFF).toByte()
        buffer[1] = (sample shr 8).toByte()
        buffer[2] = buffer[0]
        buffer[3] = buffer[1]

        val rms = AppUtils.calculateRms(buffer, buffer.size)
        assertEquals(sample.toDouble(), rms, 1.0)
    }

    @Test
    fun formatStorageSize_formatsMegabytes() {
        assertEquals("0 MB", AppUtils.formatStorageSize(0))
        assertEquals("1.0 MB", AppUtils.formatStorageSize(1024L * 1024L))
    }

    @Test
    fun escapeCsv_wrapsAndEscapesQuotes() {
        val value = "Hello, "world""
        val escaped = AppUtils.escapeCsv(value)
        assertTrue(escaped.startsWith("""))
        assertTrue(escaped.endsWith("""))
        assertTrue(escaped.contains(""""))
    }
}
