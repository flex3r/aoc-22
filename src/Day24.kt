suspend fun main() {
    val testInput = readInput("Day24_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>): Int {
    val (grid, start, end) = parseGrid(input)
    return moveTime(grid, start, end)
}

private fun part2(input: List<String>): Int {
    val (grid, start, end) = parseGrid(input)
    return moveTime(grid, start, end, moveTime(grid, end, start, moveTime(grid, start, end)))
}

private fun parseGrid(input: List<String>): Triple<Grid<Char>, Vec2, Vec2> {
    val width = input.maxOf { it.length }
    val height = input.size
    val grid = Grid.fromInput(width, height) { (x, y) -> input[y][x] }
    val start = grid.getRowCells(0).first { (_, c) -> c == '.' }.first
    val end = grid.getRowCells(input.lastIndex).first { (_, c) -> c == '.' }.first
    return Triple(grid, start, end)
}

private fun moveTime(grid: Grid<Char>, start: Vec2, end: Vec2, startTime: Int = 0): Int {
    return bfs(start.withIndex(startTime)) {
        moves(grid, it)
    }.first { (_, pos) -> pos.value == end }.value.index
}

private fun moves(grid: Grid<Char>, position: IndexedValue<Vec2>): List<IndexedValue<Vec2>> {
    val time = position.index + 1
    return (position.value.neighbors + position.value)
        .filter { it in grid && grid[it] != '#' && checkBlizzards(grid, it, time) }
        .map { it.withIndex(time) }
}

private fun checkBlizzards(grid: Grid<Char>, pos: Vec2, time: Int): Boolean {
    val widthWithoutBroder = grid.width - 2
    val heightWithoutBorder = grid.height - 2
    return grid.get(x = (pos.x - 1 + time).mod(widthWithoutBroder) + 1, pos.y) != '<' &&
            grid.get(x = (pos.x - 1 - time).mod(widthWithoutBroder) + 1, pos.y) != '>' &&
            grid.get(pos.x, y = (pos.y - 1 + time).mod(heightWithoutBorder) + 1) != '^' &&
            grid.get(pos.x, y = (pos.y - 1 - time).mod(heightWithoutBorder) + 1) != 'v'
}