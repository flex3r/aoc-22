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
        .map { it.findDuplicate() }
        .sumOf { it.toPriority() }
}


private fun part2(input: List<String>): Int {
    return input
        .chunked(3)
        .map { it.findGroupBadge() }
        .sumOf { it.toPriority() }
}

private fun String.findDuplicate(): Char {
    val (first, second) = toList().chunked(size = length / 2)
    val duplicates = first intersect second.toSet()
    return duplicates.first()
}

private fun List<String>.findGroupBadge(): Char {
    val (first, second, third) = map { it.toSet() }
    val duplicates = first intersect second intersect third
    return duplicates.first()
}

// too lazy for char codes today
private val chars = ('a'..'z') + ('A'..'Z')
private fun Char.toPriority() = chars.indexOf(this) + 1