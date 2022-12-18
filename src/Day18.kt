suspend fun main() {
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>): Int {
    val points = input.map(Point3::fromString)
    return points.sumOf { point ->
        6 - point.neighbors.count { it in points }
    }
}

private fun part2(input: List<String>): Int {
    val points = input.map(Point3::fromString)
    val ranges = listOf(
        points.openRangeOf { it.x },
        points.openRangeOf { it.y },
        points.openRangeOf { it.z }
    )
    val start = Point3(ranges[0].first, ranges[1].first, ranges[2].first)
    var sides = 0
    bfs(start) { point ->
        point.neighbors
            .onEach { if (it in points) sides++ }
            .filter { it !in points && it in ranges }
    }.toList()
    return sides
}

private inline fun List<Point3>.openRangeOf(selector: (Point3) -> Int): IntRange {
    return minOf(selector) - 1..maxOf(selector) + 1
}

private operator fun List<IntRange>.contains(point: Point3) = let { (xRange, yRange, zRange) ->
    point.x in xRange && point.y in yRange && point.z in zRange
}

private data class Point3(val x: Int, val y: Int, val z: Int) {
    val neighbors
        get() = setOf(
            copy(x = x - 1),
            copy(x = x + 1),
            copy(y = y - 1),
            copy(y = y + 1),
            copy(z = z - 1),
            copy(z = z + 1)
        )

    companion object {
        fun fromString(input: String) = input.split(",").let { (x, y, z) -> Point3(x.toInt(), y.toInt(), z.toInt()) }
    }
}