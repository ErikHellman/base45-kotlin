package se.hellsoft.base45

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class Base45Test {
    @Test
    fun testEncodeDecode() {
        val allBytes = (Byte.MIN_VALUE..Byte.MAX_VALUE).map { it.toByte() }.toByteArray()
        testEncodeDecode(allBytes)
        val allButLast = (Byte.MIN_VALUE until Byte.MAX_VALUE).map { it.toByte() }.toByteArray()
        testEncodeDecode(allButLast)
        val sameRandomBytes = ByteArray(65656)
        Random(123456).nextBytes(sameRandomBytes)
        testEncodeDecode(sameRandomBytes)
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
    fun testDecodeEncode() {
        var encoded = "BB8"
        var decoded = encoded.decodeAsBase45()
        assertEquals("BB8", decoded.encodeBase45())

        encoded = "%69 VD92EX0"
        decoded = encoded.decodeAsBase45()
        assertEquals("%69 VD92EX0", decoded.encodeBase45())

        encoded = "UJCLQE7W581"
        decoded = encoded.decodeAsBase45()
        assertEquals("UJCLQE7W581", decoded.encodeBase45())
    }

    @Test
    fun testBadInputDecoding() {
        try {
            ":::".decodeAsBase45().decodeToString()
            fail("Decoding of bad data should fail!")
        } catch (e: Exception) {
            assertTrue { e is IllegalArgumentException }
            assertEquals("Not a valid base45 string!", e.message)
        }
        try {
            "xyzzy".decodeAsBase45().decodeToString()
            fail("Decoding of bad data should fail!")
        } catch (e: Exception) {
            assertTrue { e is IllegalArgumentException }
            assertEquals("Not a valid base45 string!", e.message)
        }
        try {
            "a".decodeAsBase45().decodeToString()
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
