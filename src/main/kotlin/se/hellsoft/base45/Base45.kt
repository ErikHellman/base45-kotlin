package se.hellsoft.base45

internal val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".toCharArray()
internal const val CHARSET_LENGTH = 45
internal const val CHARSET_LENGTH_SQUARED = 45 * 45

internal const val MIN_CHAR_VALUE = 32
internal const val REVERSE_CHARSET_SIZE = 59
internal val REVERSE_CHARSET = intArrayOf(
    36, -1, -1, -1, 37, 38, -1, -1, -1, -1,
    39, 40, -1, 41, 42, 43, 0, 1, 2, 3,
    4, 5, 6, 7, 8, 9, 44, -1, -1, -1,
    -1, -1, -1, 10, 11, 12, 13, 14, 15, 16,
    17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
    27, 28, 29, 30, 31, 32, 33, 34, 35
)

fun ByteArray.encodeBase45(): String {
    val map = CHARSET
    val dataSize = size
    val lastGroupSize = dataSize % 2
    val length = dataSize / 2 * 3 + if (lastGroupSize != 0) 2 else 0
    val out = CharArray(length)
    val end = dataSize - lastGroupSize
    var index = 0
    var i = 0

    while (i < end) {
        val v = (this[i++].toInt() and 0xFF shl 8) + (this[i++].toInt() and 0xFF)
        val remainder = v % CHARSET_LENGTH_SQUARED
        val e = v / CHARSET_LENGTH_SQUARED
        val c = remainder % CHARSET_LENGTH
        val d = remainder / CHARSET_LENGTH
        out[index++] = map[c]
        out[index++] = map[d]
        out[index++] = map[e]
    }

    if (lastGroupSize == 1) {
        val a = this[i].toInt() and 0xFF
        val c = a % CHARSET_LENGTH
        val d = (a - c) / CHARSET_LENGTH
        out[index++] = map[c]
        out[index] = map[d]
    }

    return out.concatToString()
}

fun String.decodeAsBase45(): ByteArray {
    val stringLength = length
    val remainder = stringLength % 3
    require(remainder != 1) { "Not a valid base45 string!" }

    val reverseMap = REVERSE_CHARSET
    val tripleGroupSize = stringLength - remainder
    val outputSize = (stringLength / 3) * 2 + if (remainder == 0) 0 else 1
    val output = ByteArray(outputSize)
    var i = 0
    var index = 0

    while (i < tripleGroupSize) {
        val c = this[i + 0].toInt() - MIN_CHAR_VALUE
        val d = this[i + 1].toInt() - MIN_CHAR_VALUE
        val e = this[i + 2].toInt() - MIN_CHAR_VALUE
        require(c in 0 until REVERSE_CHARSET_SIZE) { "Not a valid base45 string!" }
        require(d in 0 until REVERSE_CHARSET_SIZE) { "Not a valid base45 string!" }
        require(e in 0 until REVERSE_CHARSET_SIZE) { "Not a valid base45 string!" }

        val v = reverseMap.lookup(c) +
                reverseMap.lookup(d) * CHARSET_LENGTH +
                reverseMap.lookup(e) * CHARSET_LENGTH_SQUARED
        require(v in 0..65535) { "Not a valid base45 string!" }

        val b = v % 256
        val a = v / 256
        output[index + 0] = a.toByte()
        output[index + 1] = b.toByte()

        i += 3
        index += 2
    }

    if (remainder == 2) {
        val c = this[i + 0].toInt() - MIN_CHAR_VALUE
        val d = this[i + 1].toInt() - MIN_CHAR_VALUE
        require(c in 0 until REVERSE_CHARSET_SIZE) { "Not a valid base45 string!" }
        require(d in 0 until REVERSE_CHARSET_SIZE) { "Not a valid base45 string!" }

        val v = reverseMap.lookup(c) +
                reverseMap.lookup(d) * CHARSET_LENGTH
        require(v in 0..255) { "Not a valid base45 string!" }

        output[index] = v.toByte()
    }

    return output
}

@Suppress("NOTHING_TO_INLINE")
private inline fun IntArray.lookup(index: Int): Int {
    return this[index].also { value ->
        require(value != -1) { "Not a valid base45 string!" }
    }
}
