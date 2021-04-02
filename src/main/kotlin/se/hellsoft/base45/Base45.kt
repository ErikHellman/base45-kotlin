package se.hellsoft.base45

val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".toCharArray()

fun ByteArray.encodeBase45(map: CharArray = CHARSET): String {
    val mapSize = CHARSET.size
    val mapSizeSquared = mapSize * mapSize
    val dataSize = size
    val length = dataSize / 2 * 3 + if (dataSize % 2 != 0) 2 else 0
    val out = CharArray(length) { '*' }
    val end = dataSize - dataSize % 2
    var index = 0
    var i = 0

    while (i < end) {
        val v = (this[i++].toInt() and 0xFF shl 8) + (this[i++].toInt() and 0xFF)
        val remainder = v % mapSizeSquared
        val e = (v - remainder) / mapSizeSquared
        val c = remainder % mapSize
        val d = (remainder - c) / mapSize
        out[index++] = map[c]
        out[index++] = map[d]
        out[index++] = map[e]
    }

    if (dataSize - end > 0) {
        val a = this[i].toInt() and 0xFF
        val c = a % mapSize
        val d = (a - c) / mapSize
        out[index++] = map[c]
        out[index] = map[d]
    }

    return out.concatToString()
}

fun String.decodeAsBase45(map: CharArray = CHARSET): ByteArray {
    val mapSize = CHARSET.size
    val mapSizeSquared = mapSize * mapSize

    val stringLength = length
    val remainder = stringLength % 3
    val size = (stringLength / 3) * 2 + if (remainder == 0) 0 else 1
    val out = ByteArray(size)
    var i = 0
    var index = 0

    while (i < stringLength) {
        if (i < stringLength - 2) {
            val v = map.indexOf(this[i++]) + map.indexOf(this[i++]) * mapSize + map.indexOf(this[i++]) * mapSizeSquared
            val y = v % 256
            val x = (v - y) / 256
            out[index++] = x.toByte()
            out[index++] = y.toByte()
        } else {
            out[index++] = (map.indexOf(this[i++]) + map.indexOf(this[i++]) * mapSize).toByte()
        }
    }

    return out
}
