package se.hellsoft.base45

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class Base45Test {
    @Test
    fun testEncodeDecode() {
        val allBytes = (0..255).map { it.toByte() }.toByteArray()
        testEncodeDecode(allBytes)
        val allButLast = (0..254).map { it.toByte() }.toByteArray()
        testEncodeDecode(allButLast)
        val singleByte = byteArrayOf(0xf)
        testEncodeDecode(singleByte)
        val twoBytes = byteArrayOf(0xf, 0x0)
        testEncodeDecode(twoBytes)
        val threeBytes = byteArrayOf(0xf, 0x9, 0x0)
        testEncodeDecode(threeBytes)

        testEncodeDecode("Hello!!")
        testEncodeDecode("AB")
        testEncodeDecode("base-45")
        testEncodeDecode("en längre sträng med konstiga tecken: \u2713\u0606\u0608\u13C1")
    }

    @Test
    fun testBadInputDecoding() {
        try {
            ":::".decodeAsBase45()
            fail("Decoding of bad data should fail!")
        } catch (e: Exception) {
            assertTrue { e is IllegalArgumentException }
            assertEquals("Not a valid base45 string!", e.message)
        }
        try {
            "xyzzy".decodeAsBase45()
            fail("Decoding of bad data should fail!")
        } catch (e: Exception) {
            assertTrue { e is IllegalArgumentException }
            assertEquals("Not a valid base45 string!", e.message)
        }
        try {
            "a".decodeAsBase45()
            fail("Decoding of bad data should fail!")
        } catch (e: Exception) {
            assertTrue { e is IllegalArgumentException }
            assertEquals("Not a valid base45 string!", e.message)
        }
    }

    private fun testEncodeDecode(testData: String) {
        val encoded = testData.encodeToByteArray().encodeBase45()
        val decoded = encoded.decodeAsBase45().decodeToString()
        assertEquals(testData, decoded)
    }

    private fun testEncodeDecode(bytes: ByteArray) {
        val encoded = bytes.encodeBase45()
        val decoded = encoded.decodeAsBase45()
        assertTrue { decoded.contentEquals(bytes) }
    }
}
