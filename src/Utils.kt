import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Stack
import kotlin.time.measureTime

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

fun readAllInput(name: String) = File("src", "$name.txt").readText()

inline fun <T> Collection<T>.partitionBy(predicate: (T) -> Boolean): List<List<T>> {
    return fold(mutableListOf(mutableListOf())) { acc: MutableList<MutableList<T>>, it ->
        when {
            predicate(it) -> acc.add(mutableListOf())
            else -> acc.last() += it
        }
        acc
    }
}

fun <T> List<List<T>>.transpose(): List<List<T>> = this[0].indices.map { x -> indices.map { y -> this[y][x] } }

fun <T> combinations(values: List<T>, m: Int) = sequence {
    val n = values.size
    val result = MutableList(m) { values[0] }
    val stack = Stack<Int>()
    stack.push(0)
    while (stack.isNotEmpty()) {
        var resIndex = stack.lastIndex
        var arrIndex = stack.pop()

        while (arrIndex < n) {
            result[resIndex++] = values[arrIndex++]
            stack.push(arrIndex)

            if (resIndex == m) {
                yield(result.toList())
                break
            }
        }
    }
}

fun <T> bfs(start: T, neighbors: (T) -> List<T>) = sequence {
    val queue = ArrayDeque(listOf(start.withIndex(index = 0)))
    val seen = mutableSetOf(start)
    while (queue.isNotEmpty()) {
        val (index, current) = queue.removeFirst().also { yield(it) }
        neighbors(current).forEach { neighbor ->
            if (seen.add(neighbor)) {
                queue.add(neighbor.withIndex(index = index + 1))
            }
        }
    }
}

fun <T> T.withIndex(index: Int) = IndexedValue(index, value = this)

suspend inline fun <T> measureAndPrintResult(crossinline block: suspend () -> T) = withContext(Dispatchers.Default) {
    measureTime {
        println(block())
    }.also { println("Took $it") }
}

fun <T> List<T>.nth(n: Int) = this[n % size]

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun Iterable<Int>.product(): Int = reduce { acc, i -> acc * i }

data class Vec2(val x: Int, val y: Int) {
    val neighbors: List<Vec2> get() = listOf(
        copy(x = x + 1),
        copy(x = x - 1),
        copy(y = y + 1),
        copy(y = y - 1),
    )
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun div(scalar: Int) = Vec2(x / scalar, y / scalar)
}

fun Iterable<Vec2>.maxWidth() = maxOf { it.x } - minOf { it.x } + 1
fun Iterable<Vec2>.maxHeight() = maxOf { it.y } - minOf { it.y } + 1

data class Grid<T>(val values: List<T>, val width: Int, val height: Int) {
    operator fun contains(other: Vec2) = other.x in (0 until width) && other.y in (0 until height)
    fun get(x: Int, y: Int): T = values[y * width + x]
    operator fun get(position: Vec2): T = get(position.x, position.y)
    fun getRowCells(y: Int) = (0 until width).map { x -> Vec2(x, y).let { it to get(it) }  }
    fun getColumnCells(x: Int) = (0 until height).map { y -> Vec2(x, y).let { it to get(it) } }

    companion object {
        inline fun <reified T> fromInput(width: Int, height: Int, valueAt: (Vec2) -> T): Grid<T> {
            val values = List(width * height) { idx -> valueAt(Vec2(idx % width, idx / width)) }
            return Grid(values, width, height)
        }
    }
}