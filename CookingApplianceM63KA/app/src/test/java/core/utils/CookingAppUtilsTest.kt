package core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CookingAppUtilsTest {

    @Test
    fun testGetTimeInHours() {
        val hour = CookingAppUtils.getTimeInHours(3600)
        assertEquals("1", hour)
    }

    @Test
    fun testGetTimeInHours_lessThan3600() {
        val hour = CookingAppUtils.getTimeInHours(3000)
        assertEquals("0", hour)
    }


}