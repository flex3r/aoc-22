import kotlin.math.pow

suspend fun main() {
    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    measureAndPrintResult {
        part1(input)
    }
}

private fun part1(input: List<String>): String {
    return encode(input.sumOf(::decode))
}

private fun decode(input: String) = input.withIndex().sumOf { (idx, c) ->
    val place = 5.0.pow(input.lastIndex - idx).toLong()
    val value = when (c) {
        '0', '1', '2' -> c.digitToInt()
        '-' -> -1
        '=' -> -2
        else -> error("xd")
    }
    place * value
}

private fun encode(value: Long): String = value.toString(radix = 5)
    .map(Char::digitToInt)
    .foldRight(emptyList<Int>() to 0) { i, (result, acc) ->
        val withAcc = i + acc
        when {
            withAcc >= 3 -> result + (withAcc - 5) to 1
            else -> result + withAcc to 0
        }
    }
    .first
    .reversed()
    .joinToString(separator = "") {
        when (it) {
            0, 1, 2 -> "${it.digitToChar()}"
            -1 -> "-"
            -2 -> "="
            else -> error("xd")
        }
    }