import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val testInput2 = readInput("Day09_test2")
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val moves = parseInput(input)
    val head = Knot()
    var tail = Knot()
    val positions = mutableSetOf(tail)
    moves.forEach { move ->
        head.move(move)
        tail = head.nextPosition(tail).also { positions += it }
    }
    return positions.size
}

private fun part2(input: List<String>): Int {
    val moves = parseInput(input)
    val knots = MutableList(10) { Knot() }
    val positions = mutableSetOf(knots.last())
    moves.forEach { move ->
        knots.first().move(move)
        (0 until knots.lastIndex).forEach {
            knots[it + 1] = knots[it].nextPosition(knots[it + 1])
        }
        positions += knots.last()
    }
    return positions.size
}

private fun parseInput(input: List<String>): List<Pair<Int, Int>> {
    return input.flatMap {
        val (direction, amount) = it.split(" ")
        val move = when (direction) {
            "U" -> 0 to 1
            "D" -> 0 to -1
            "L" -> -1 to 0
            else -> 1 to 0
        }
        (0 until amount.toInt()).map { move }
    }
}

private data class Knot(var x: Int = 0, var y: Int = 0) {
    private fun isTouching(other: Knot): Boolean = abs(x - other.x) <= 1 && abs(y - other.y) <= 1
    fun move(move: Pair<Int, Int>) {
        x += move.first
        y += move.second
    }
    fun nextPosition(other: Knot): Knot = when {
        isTouching(other) -> other
        else -> {
            val xOffset = (x - other.x).sign
            val yOffset = (y - other.y).sign
            other.copy(x = other.x + xOffset, y = other.y + yOffset)
        }
    }
}