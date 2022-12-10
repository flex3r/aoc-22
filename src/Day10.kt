import kotlin.math.absoluteValue

fun main() {
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)

    val part2Test = """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
    """.trimIndent()
    check(part2(testInput) == part2Test)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val cycles = calculateCycles(input)
    return (20..220 step 40).sumOf { cycles[it] * it }
}

private fun part2(input: List<String>): String {
    val cycles = calculateCycles(input)
    return (0 until 240)
        .joinToString("") { time ->
            when {
                (cycles[time + 1] - time % 40).absoluteValue <= 1 -> "#"
                else -> "."
            }
        }
        .chunked(40)
        .joinToString("\n")
}

private fun calculateCycles(input: List<String>): List<Int> {
    return listOf(1) + input.flatMap {
        when (it) {
            "noop" -> listOf(0)
            else -> listOf(0, it.substringAfter("addx ").toInt())
        }
    }.runningFold(1) { acc, value -> acc + value }
}