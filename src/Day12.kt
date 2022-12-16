fun main() {
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (start, end, hills) = parseHills(input)
    return bfs(start) { neighbors(hills).filter { it.height - height <= 1 } }
        .first { (_, it) -> hills[it.y][it.x] == end }
        .index
}
private fun part2(input: List<String>): Int {
    val (_, end, hills) = parseHills(input)
    return bfs(end) { neighbors(hills).filter { height - it.height <= 1 } }
        .first { (_, it) -> hills[it.y][it.x].char == 'a' }
        .index
}

private fun parseHills(input: List<String>): HillData {
    lateinit var start: Hill
    lateinit var end: Hill
    val hills = input.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            when (c) {
                'S' -> Hill('a', x, y).also { start = it }
                'E' -> Hill('z', x, y).also { end = it }
                else -> Hill(c, x, y)
            }
        }
    }
    return HillData(start, end, hills)
}

private data class HillData(val start: Hill, val end: Hill, val hills: List<List<Hill>>)
private data class Hill(val char: Char, val x: Int, val y: Int, val height: Int = char.code) {
    fun neighbors(hills: List<List<Hill>>)= buildList {
        if (y - 1 >= 0) add(hills[y - 1][x])
        if (y + 1 <= hills.lastIndex) add(hills[y + 1][x])
        if (x - 1 >= 0) add(hills[y][x - 1])
        if (x + 1 <= hills[y].lastIndex) add(hills[y][x + 1])
    }
}