fun main() {
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input
        .partitionBy { it.isEmpty() }
        .maxOfOrNull { elf -> elf.sumOf { it.toInt() } } ?: 0
}


private fun part2(input: List<String>): Int {
    return input.partitionBy { it.isEmpty() }
        .map { elf -> elf.sumOf { it.toInt() } }
        .sortedDescending()
        .take(3)
        .sum()
}
