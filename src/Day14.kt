import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (cave, abyssStart) = parseCave(input)

    var sandIsLost = false
    while(!sandIsLost) {
        var (x,y) = CavePos(500, 0)
        var placed = false
        while(!placed) {
            if (y >= abyssStart) {
                sandIsLost = true
                break
            }

            when (CaveMaterial.AIR) {
                cave[y + 1][x] -> y++
                cave[y + 1][x - 1] -> { y++; x-- }
                cave[y + 1][x + 1] -> { y++; x++ }
                else -> {
                    placed = true
                    cave[y][x] = CaveMaterial.SAND
                }
            }
        }
    }
    return cave.sandCount
}

private fun part2(input: List<String>): Int {
    val (cave, abyssStart) = parseCave(input)
    val wallY = abyssStart + 2
    cave[wallY].indices.forEach { x -> cave[wallY][x] = CaveMaterial.ROCK }

    var sourceBlocked = false
    while(!sourceBlocked) {
        var (x, y) = CavePos(500, 0)
        var placed = false
        while(!placed) {
            when (CaveMaterial.AIR) {
                cave[y + 1][x] -> y++
                cave[y + 1][x - 1] -> { y++; x-- }
                cave[y + 1][x + 1] -> { y++; x++ }
                else -> {
                    placed = true
                    cave[y][x] = CaveMaterial.SAND
                    if (x == 500 && y == 0) sourceBlocked = true
                }
            }
        }
    }
    return cave.sandCount
}

private fun parseCave(input: List<String>): CaveData {
    val paths = input.flatMap { line ->
        line.split(" -> ").windowed(2).map { (start, end) ->
            val (startX, startY) = start.split(",")
            val (endX, endY) = end.split(",")
            CavePath(CavePos(startX.toInt(), startY.toInt()), CavePos(endX.toInt(), endY.toInt()))
        }
    }
    val cave = MutableList(200) { MutableList(750) { CaveMaterial.AIR } }
    cave[0][500] = CaveMaterial.SOURCE
    val abyssStart = paths.maxOfOrNull { max(it.start.y, it.end.y) }!!
    paths.forEach { (start, end) ->
        val minY = min(start.y, end.y)
        val minX = min(start.x, end.x)
        val maxY = max(start.y, end.y)
        val maxX = max(start.x, end.x)
        (minX..maxX).forEach { x ->
            (minY..maxY).forEach { y ->
                cave[y][x] = CaveMaterial.ROCK
            }
        }
    }
    return CaveData(cave, abyssStart)
}

private enum class CaveMaterial {
    AIR,
    ROCK,
    SAND,
    SOURCE
}

private data class CavePos(val x: Int, val y: Int)
private data class CavePath(val start: CavePos, val end: CavePos)
private data class CaveData(val paths: MutableList<MutableList<CaveMaterial>>, val abyssStart: Int)
private val List<List<CaveMaterial>>.sandCount get() = sumOf { line -> line.count { it == CaveMaterial.SAND } }