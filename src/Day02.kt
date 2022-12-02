fun main() {
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.map(::parseLine)
        .sumOf { (opponent, player) ->
            val baseScore = player + 1
            val gameScore = when ((player - opponent).mod(3)) {
                0 -> 3
                1 -> 6
                else -> 0
            }
            baseScore + gameScore
        }
}


private fun part2(input: List<String>): Int {
    return input.map(::parseLine)
        .sumOf { (opponent, player) ->
            val gameScore = player * 3
            val move = when (gameScore) {
                0 -> 2
                3 -> 0
                else -> 1
            }
            val baseScore = (move + opponent).mod(3) + 1
            baseScore + gameScore
        }
}

private fun parseLine(line: String): Pair<Int, Int> = line.first() - 'A' to line.last() - 'X'