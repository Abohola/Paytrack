package com.paytrack.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {
    @Test fun parsesCommonDecimalFormats() {
        assertEquals(1250L, Money.parseMinor("12.50"))
        assertEquals(1250L, Money.parseMinor("12,50"))
        assertEquals(123456L, Money.parseMinor("1,234.56"))
        assertEquals(123456L, Money.parseMinor("1.234,56"))
    }

    @Test fun rejectsInvalidAndNonPositiveValues() {
        assertNull(Money.parseMinor(""))
        assertNull(Money.parseMinor("hello"))
        assertNull(Money.parseMinor("0"))
        assertNull(Money.parseMinor("-5"))
    }
}
