fun main() {
    val testInput = readAllInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readAllInput("Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: String): Int {
    return endOfMarkerPosition(input)
}

private fun part2(input: String): Int {
    return endOfMarkerPosition(input, distinctCharAmount = 14)
}

private fun endOfMarkerPosition(input: String, distinctCharAmount: Int = 4): Int {
    return input
        .windowedSequence(distinctCharAmount)
        .indexOfFirst { it.toSet().size == distinctCharAmount } + distinctCharAmount
}