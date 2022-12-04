fun main() {
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.map(::parseLine)
        .count { (first, second) -> first fullyIn second || second fullyIn first }
}

private fun part2(input: List<String>): Int {
    return input.map(::parseLine)
        .count { (first, second) -> first in second || second in first }
}

private fun parseLine(line: String): Pair<IntRange, IntRange> {
    val (first, second) = line.split(",").map {
        val (start, end) = it.split("-")
        start.toInt()..end.toInt()
    }
    return first to second
}

private infix fun IntRange.fullyIn(other: IntRange): Boolean {
    return start in other && endInclusive in other
}

private operator fun IntRange.contains(other: IntRange): Boolean {
    return start in other || endInclusive in other
}