import java.util.Stack

fun main() {
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val (crateInput, procedure) = input.partitionBy { it.isEmpty() }
    val stacks = parseCrates(crateInput)
    procedure.forEachAction { (amount, from, to) ->
        repeat(amount) {
            val crate = stacks[from - 1].pop()
            stacks[to - 1].push(crate)
        }
    }
    return stacks.joinToString(separator = "") { it.peek().toString() }
}

private fun part2(input: List<String>): String {
    val (crateInput, procedure) = input.partitionBy { it.isEmpty() }
    val stacks = parseCrates(crateInput)
    procedure.forEachAction { (amount, from, to) ->
        val cratesToPush = (0 until amount).map { stacks[from - 1].pop() }.reversed()
        stacks[to - 1].addAll(cratesToPush)
    }
    return stacks.joinToString(separator = "") { it.peek().toString() }
}

private fun parseCrates(rows: List<String>): List<Stack<Char>> {
    val lastRow = rows.last()
    val stacks = List(lastRow.substringAfterLast(" ").toInt()) { Stack<Char>() }
    rows.dropLast(1)
        .reversed()
        .forEach { row ->
            row.chunked(size = 4)
                .forEachIndexed { chunkIdx, crate ->
                    if (crate.isBlank()) return@forEachIndexed
                    stacks[chunkIdx].push(crate[1])
                }
        }
    return stacks
}

private val procedureRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()
private fun parseAction(action: String): List<Int> = procedureRegex.find(action)!!.groupValues.drop(1).map { it.toInt() }
private fun List<String>.forEachAction(action: (List<Int>) -> Unit) = forEach { action(parseAction(it)) }