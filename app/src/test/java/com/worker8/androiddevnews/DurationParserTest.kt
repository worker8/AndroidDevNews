package com.worker8.androiddevnews

import com.worker8.androiddevnews.util.DurationParser
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DurationParserTest {
    @Test
    fun twoInput() {
        val input = "12:34"
        val result = DurationParser.parse(input)
        assertEquals("12m 34s", result)
    }

    @Test
    fun twoInputZeroes() {
        var input = "00:34"
        var result = DurationParser.parse(input)
        assertEquals("34s", result)

        input = "01:00"
        result = DurationParser.parse(input)
        assertEquals("1m", result)

        input = "00:00"
        result = DurationParser.parse(input)
        assertEquals("0s", result)
    }

    @Test
    fun threeInput() {
        val input = "10:12:34"
        val result = DurationParser.parse(input)
        assertEquals("10h 12m 34s", result)
    }

    @Test
    fun threeInputZeroes() {
        var input = "00:12:34"
        var result = DurationParser.parse(input)
        assertEquals("12m 34s", result)

        input = "09:00:34"
        result = DurationParser.parse(input)
        assertEquals("9h 34s", result)

        input = "09:00:00"
        result = DurationParser.parse(input)
        assertEquals("9h", result)

        input = "09:25:00"
        result = DurationParser.parse(input)
        assertEquals("9h 25m", result)

        input = "00:00:59"
        result = DurationParser.parse(input)
        assertEquals("59s", result)
    }
}