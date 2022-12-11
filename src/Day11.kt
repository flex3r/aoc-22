fun main() {
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long = run(input, rounds = 20) { level, _ -> level / 3 }
private fun part2(input: List<String>): Long = run(input, rounds = 10_000) { level, lcm -> level % lcm }
private fun run(input: List<String>, rounds: Int, keepingItManageable: (Long, Long) -> Long): Long {
    val monkeys = parseMonkeys(input)
    val lcm = monkeys.map(Monkey::testDivisor).reduce(Long::times)
    repeat(rounds) {
        monkeys.forEach { monkey ->
            monkey.items.forEach { item ->
                monkey.numberOfInspections++
                val newItemLevel = keepingItManageable(monkey.operation(item), lcm)
                val newIndex =
                    if (newItemLevel % monkey.testDivisor == 0L) monkey.testIndexTrue else monkey.testIndexFalse
                monkeys[newIndex].items.add(newItemLevel)
            }
            monkey.items.clear()
        }
    }

    return monkeys.map(Monkey::numberOfInspections).sortedDescending().take(2).reduce(Long::times)
}

private fun parseMonkeys(input: List<String>): List<Monkey> {
    val grouped = input.partitionBy { it.isEmpty() }
    return grouped.map { lines ->
        val argument = lines[2].substringAfterLast(" ").takeIf { it != "old" }?.toLong()
        Monkey(
            items = lines[1].substringAfter("items: ").split(", ").map { it.toLong() }.toMutableList(),
            testDivisor = lines[3].substringAfter("by ").toLong(),
            testIndexTrue = lines[4].substringAfter("monkey ").toInt(),
            testIndexFalse = lines[5].substringAfter("monkey ").toInt(),
            operation = when (lines[2].substringAfter("old ").first()) {
                '*' -> { old: Long -> old * (argument ?: old) }
                else -> { old: Long -> old + (argument ?: old) }
            }
        )
    }
}

private data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val testDivisor: Long,
    val testIndexTrue: Int,
    val testIndexFalse: Int,
    var numberOfInspections: Long = 0
)