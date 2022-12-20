suspend fun main() {
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>) = input
    .map { it.toLong() }
    .reorder()
    .getResult()

private fun part2(input: List<String>) = input
    .map { it.toLong() * 811589153L }
    .reorder(times = 10)
    .getResult()

private fun List<Long>.getResult(): Long = indexOf(0).let { zeroIndex -> listOf(1000, 2000, 3000).sumOf { nth(zeroIndex + it) } }
private fun List<Long>.reorder(times: Int = 1): List<Long> {
    val withOriginalIndex = withIndex()
    val mutable = withOriginalIndex.toMutableList()
    repeat(times) {
        withOriginalIndex.forEach {
            val idx = mutable.indexOf(it)
            mutable.removeAt(idx)
            val newIndex = (idx + it.value).mod(mutable.size)
            mutable.add(newIndex, it)
        }
    }
    return mutable.map { it.value }
}