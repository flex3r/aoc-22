import kotlin.math.max

fun main() {
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val trees = parseTrees(input)
    val runningMaxSides = listOf(
        trees.runningMaxStart(),
        trees.runningMaxEnd(),
        trees.runningMaxTop(),
        trees.runningMaxBottom(),
    )

    return trees.withIndex().sumOf { (i, row) ->
        row.withIndex().count { (j, tree) -> runningMaxSides.any { it[i][j] < tree } }
    }
}

private fun part2(input: List<String>): Int {
    val trees = parseTrees(input)
    val scenicValues = listOf(
        trees.scenicValuesStart(),
        trees.scenicValuesEnd(),
        trees.scenicValuesTop(),
        trees.scenicValuesBottom(),
    )

    return trees.withIndex().flatMap { (i, row) ->
        row.withIndex().map { (j, _) ->
            scenicValues
                .map { it[i][j] }
                .reduce { acc, it -> acc * it }
        }
    }.max()
}

private fun parseTrees(input: List<String>): List<List<Int>> = input.map { row -> row.map { it.digitToInt() } }

private fun List<List<Int>>.runningMaxStart(): List<List<Int>> = map { row ->
    row.dropLast(1)
        .runningFold(-1) { acc, tree -> max(acc, tree) }
}
private fun List<List<Int>>.runningMaxEnd(): List<List<Int>> = map { row ->
    row.drop(1)
        .reversed()
        .runningFold(-1) { acc, tree -> max(acc, tree) }
        .reversed()
}
private fun List<List<Int>>.runningMaxTop(): List<List<Int>> = transpose().runningMaxStart().transpose()
private fun List<List<Int>>.runningMaxBottom(): List<List<Int>> = transpose().runningMaxEnd().transpose()

private fun List<List<Int>>.scenicValuesStart(): List<List<Int>> = map { it.scenicDistances() }
private fun List<List<Int>>.scenicValuesEnd(): List<List<Int>> = map { it.reversed().scenicDistances().reversed() }
private fun List<List<Int>>.scenicValuesTop(): List<List<Int>> = transpose().scenicValuesStart().transpose()
private fun List<List<Int>>.scenicValuesBottom(): List<List<Int>> = transpose().scenicValuesEnd().transpose()
private fun List<Int>.scenicDistances(): List<Int> {
    return mapIndexed { idx, tree ->
        slice(idx + 1..lastIndex)
            .indexOfFirst { it >= tree }
            .takeIf { it != -1 }
            ?.inc()
            ?: (lastIndex - idx)
    }
}