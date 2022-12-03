fun main() {
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input
        .map { it.chunked(size = it.length / 2).findDuplicate() }
        .sumOf { it.toPriority() }
}

private fun part2(input: List<String>): Int {
    return input
        .chunked(3)
        .map { it.findDuplicate() }
        .sumOf { it.toPriority() }
}

private fun List<String>.findDuplicate(): Char {
    return map { it.toSet() }
        .reduce { acc, chars ->
            acc intersect chars
        }.first()
}

// too lazy for char codes today
private val chars = ('a'..'z') + ('A'..'Z')
private fun Char.toPriority() = chars.indexOf(this) + 1
