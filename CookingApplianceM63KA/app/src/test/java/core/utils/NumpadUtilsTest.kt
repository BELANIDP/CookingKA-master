package core.utils

import org.junit.Assert.*

import org.junit.Test

class NumpadUtilsTest {
    @Test
    fun testConvertTo24H_ForAM() {
        val result = NumpadUtils.convertTo24H("0100AM")
        assertEquals("0100", result)

    }

    @Test
    fun testConvertTo24H_ForPM() {
        val result = NumpadUtils.convertTo24H("0100PM")
        assertEquals("1300", result)

    }

    @Test
    fun testConvertTo12H_resultAm() {
        val result = NumpadUtils.convertTo12("0100")
        assertEquals("0100AM", result)

    }

    @Test
    fun testConvertTo12H_resultPm() {
        val result = NumpadUtils.convertTo12("1800")
        assertEquals("0600PM", result)

    }
}