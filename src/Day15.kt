import kotlin.math.abs

fun main() {
    val testInput = readInput("Day15_test")
    check(part1(testInput, rowToCheck = 10) == 26)
    check(part2(testInput, maxY = 20) == 56000011L)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, rowToCheck: Int = 2000000): Int = buildSet {
    parseSensors(input).forEach { (sensor, beacon) ->
        val distance = sensor.manhatten(beacon)
        val distanceToRow = abs(sensor.y - rowToCheck)
        val width = distance - distanceToRow
        (sensor.x - width..sensor.x + width).forEach { x ->
            val pos = Pos(x, rowToCheck)
            if (pos != beacon) {
                add(x)
            }
        }
    }
}.size

private fun part2(input: List<String>, maxY: Int = 4000000): Long {
    val sensors = parseSensors(input)
    (0..maxY)
        .asSequence()
        .map { y ->
            sensors.mapNotNull { (sensor, beacon) ->
                val distance = sensor.manhatten(beacon)
                val distanceToY = abs(sensor.y - y)
                (distance - distanceToY)
                    .takeIf { it > 0 }
                    ?.let { sensor.x - it..sensor.x + it }
            }.sortedBy { it.first }
        }
        .forEachIndexed { y, ranges ->
            var maxRangeEnd = ranges.first().last
            ranges.forEach { otherRange ->
                when {
                    otherRange.first > maxRangeEnd -> return (maxRangeEnd + 1) * 4_000_000L + y
                    otherRange.last > maxRangeEnd -> maxRangeEnd = otherRange.last
                }
            }
        }

    return -1
}

private fun parseSensors(input: List<String>): List<Pair<Pos, Pos>> {
    return input.map { line ->
        val (sensorPart, beaconPart) = line.split(":")
        parsePos(sensorPart) to parsePos(beaconPart)
    }
}

private fun parsePos(part: String): Pos {
    val (xPart, yPart) = part.split(",")
    val x = xPart.substringAfter("x=").toInt()
    val y = yPart.substringAfter("y=").toInt()
    return Pos(x, y)
}

private data class Pos(val x: Int, val y: Int)

private fun Pos.manhatten(other: Pos) = abs(x - other.x) + abs(y - other.y)