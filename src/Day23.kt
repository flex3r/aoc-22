import java.util.Collections

suspend fun main() {
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>): Int {
    val elves = parseElves(input)
    val movedElves = (0 until 10).fold(elves) { acc, i ->
        moveElves(acc, i)
    }

    return movedElves.maxWidth() * movedElves.maxHeight() - movedElves.size
}

private fun part2(input: List<String>): Int {
    var elves = parseElves(input)
    var rounds = 0
    while (true) {
        val newElves = moveElves(elves, rounds++)
        if (elves == newElves) {
            return rounds
        }
        elves = newElves
    }
}

private fun parseElves(input: List<String>): Set<Vec2> {
    return input.reversed().flatMapIndexed { rowIdx, row ->
        row.mapIndexed { columnIdx, c ->
            Vec2(columnIdx, rowIdx).takeIf { c == '#' }
        }
    }.filterNotNull().toSet()
}

private data class ProposedMove(val checks: List<Vec2>) {
    val move get() = checks[0]
}

private val proposedMoves = listOf(
    ProposedMove(listOf(Vec2(0, 1), Vec2(-1, 1), Vec2(1, 1))),
    ProposedMove(listOf(Vec2(0, -1), Vec2(-1, -1), Vec2(1, -1))),
    ProposedMove(listOf(Vec2(-1, 0), Vec2(-1, -1), Vec2(-1, 1))),
    ProposedMove(listOf(Vec2(1, 0), Vec2(1, -1), Vec2(1, 1))),
)

private fun proposedMovesForRound(pos: Vec2, roundNumber: Int): List<ProposedMove> {
    return proposedMoves.toMutableList()
        .also { Collections.rotate(it, -roundNumber) }
        .map { it.copy(checks = it.checks.map { v -> v + pos }) }
}

private fun moveElves(elves: Set<Vec2>, roundNumber: Int) = elves.groupBy { currentPosition ->
    val proposedMoves = proposedMovesForRound(currentPosition, roundNumber)
    when {
        proposedMoves.all { move -> move.checks.none { it in elves } } -> currentPosition
        else -> proposedMoves
            .firstOrNull { move -> move.checks.none { it in elves } }
            ?.move ?: currentPosition
    }
}.flatMap { (newPosition, oldPositions) ->
    when (oldPositions.size) {
        1 -> listOf(newPosition)
        else -> oldPositions
    }
}.toSet()