import kotlin.math.absoluteValue

suspend fun main() {
    val testInput = readInput("Day17_test")
    check(part1(testInput.first()) == 3068)
    check(part2(testInput.first()) == 1514285714288L)

    val input = readInput("Day17")
    measureAndPrintResult {
        part1(input.first())
    }
    measureAndPrintResult {
        part2(input.first())
    }
}

private fun part1(input: String): Int {
    val jets = parseJets(input)
    val (cave, _, _) = simulateSteps(2022, jets, Step(cave = wall.toMutableList()))
    return cave.height
}

private fun part2(input: String, targetBlocks: Long = 1000000000000): Long {
    val jets = parseJets(input)
    val cache = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    var step = Step(wall.toMutableList())
    while (true) {
        step = simulateStep(jets, step)
        val state = step.blockIndex to step.jetIndex
        if (state !in cache) {
            cache[state] = step.blockCount - 1 to step.cave.height
            continue
        }

        val (blocksAtStartOfLoop, heightAtStartOfLoop) = cache.getValue(state)
        val blocksPerLoop = step.blockCount - 1L - blocksAtStartOfLoop
        val heightPerLoop = step.cave.height - heightAtStartOfLoop
        val loopsNeeded = (targetBlocks - blocksAtStartOfLoop) / blocksPerLoop
        val remainingBlocks = (targetBlocks - blocksAtStartOfLoop) % blocksPerLoop
        val loopedHeight = (heightPerLoop * (loopsNeeded - 1L)) - 1L
        val (cave, _, _) = simulateSteps(remainingBlocks.toInt(), jets, step)
        return cave.height + loopedHeight
    }
}

data class Step(val cave: MutableList<Point>, val blockCount: Int = 0, val jetCount: Int = 0, val blockIndex: Int = 0, val jetIndex: Int = 0)

private fun simulateSteps(blocks: Int, jets: List<Point>, previous: Step): Step {
    return (0 until blocks).fold(previous) { acc, _ ->
        simulateStep(jets, acc)
    }
}

private fun simulateStep(jets: List<Point>, previous: Step): Step {
    val cave = previous.cave
    var blockCount = previous.blockCount
    var jetCount = previous.jetCount
    var shape = shapes.nth(blockCount++).startingPosition(cave.minY)
    do {
        val movedShape = shape + jets.nth(jetCount++)
        if (movedShape inBoundsOf 0..6 && !movedShape.collides(cave)) {
            shape = movedShape
        }

        shape = shape.moveDown()
    } while (!shape.collides(cave))
    cave += shape.moveUp()
    return Step(cave, blockCount, jetCount, blockCount % shapes.size, jetCount % jets.size)
}

private fun parseJets(input: String): List<Point> = input.map {
    when (it) {
        '>' -> Point(1, 0)
        else -> Point(-1, 0)
    }
}

private val wall = List(7) { Point(it, 0) }
private val shapes = listOf(
    setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)),
    setOf(Point(1, 0), Point(0, -1), Point(1, -1), Point(2, -1), Point(1, -2)),
    setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, -1), Point(2, -2)),
    setOf(Point(0, 0), Point(0, -1), Point(0, -2), Point(0, -3)),
    setOf(Point(0, 0), Point(1, 0), Point(0, -1), Point(1, -1))
)

private fun Set<Point>.startingPosition(minY: Int): Set<Point> = map {
    it + Point(2, minY - 4)
}.toSet()

private fun Set<Point>.moveDown(): Set<Point> = map {
    it + Point(0, 1)
}.toSet()

private fun Set<Point>.moveUp(): Set<Point> = map {
    it + Point(0, -1)
}.toSet()

private operator fun Set<Point>.plus(other: Point): Set<Point> = map {
    it + other
}.toSet()

private fun Set<Point>.collides(other: Collection<Point>) = intersect(other.toSet()).isNotEmpty()
private infix fun Set<Point>.inBoundsOf(range: IntRange) = all { it.x in range }
private val List<Point>.minY get() = minOf { it.y }
private val List<Point>.height get() = minY.absoluteValue

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}