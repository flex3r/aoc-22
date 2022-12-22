suspend fun main() {
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 6032)
    check(part2(testInput) == 5031)

    val input = readInput("Day22")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>): Int {
    val (mapInput, instructionInput) = input.partitionBy { it.isEmpty() }
    val grid = parseGrid(mapInput)
    val instructions = parseInstructions(instructionInput.first())
    return walkGrid(grid, instructions)
}

private fun part2(input: List<String>): Int {
    val (mapInput, instructionInput) = input.partitionBy { it.isEmpty() }
    val grid = parseGrid(mapInput)
    val instructions = parseInstructions(instructionInput.first())
    val isTest = grid.width == 16
    val cubeSides: Map<Vec2, Int> = buildMap {
        if (isTest) {
            putAll((0..3).flatMap { x -> (4..7).map { y -> Vec2(x, y) to 1 } })
            putAll((4..7).flatMap { x -> (4..7).map { y -> Vec2(x, y) to 2 } })
            putAll((8..11).flatMap { x -> (4..7).map { y -> Vec2(x, y) to 3 } })
            putAll((8..11).flatMap { x -> (0..3).map { y -> Vec2(x, y) to 4 } })
            putAll((8..11).flatMap { x -> (8..11).map { y -> Vec2(x, y) to 5 } })
            putAll((12..15).flatMap { x -> (8..11).map { y -> Vec2(x, y) to 6 } })
        } else {
            putAll((100..149).flatMap { x -> (0..49).map { y -> Vec2(x, y) to 1 } })
            putAll((50..99).flatMap { x -> (0..49).map { y -> Vec2(x, y) to 2 } })
            putAll((50..99).flatMap { x -> (50..99).map { y -> Vec2(x, y) to 3 } })
            putAll((50..99).flatMap { x -> (100..149).map { y -> Vec2(x, y) to 4 } })
            putAll((0..49).flatMap { x -> (100..149).map { y -> Vec2(x, y) to 5 } })
            putAll((0..49).flatMap { x -> (150..199).map { y -> Vec2(x, y) to 6 } })
        }
    }

    val rules = buildList {
        if (isTest) {
            add(Rule(cubeSide = 1, direction = Direction.Left, newDirection = Direction.Up, transformPosition = { pos -> Vec2(12 + (7 - pos.y), 11) }))
            add(Rule(cubeSide = 1, direction = Direction.Up, newDirection = Direction.Down, transformPosition = { pos -> Vec2(8 + (3 - pos.x), 0) }))
            add(Rule(cubeSide = 1, direction = Direction.Down, newDirection = Direction.Up, transformPosition = { pos -> Vec2(8 + (3 - pos.x), 11) }))

            add(Rule(cubeSide = 2, direction = Direction.Up, newDirection = Direction.Right, transformPosition = { pos -> Vec2(8, pos.x - 4) }))
            add(Rule(cubeSide = 2, direction = Direction.Down, newDirection = Direction.Right, transformPosition = { pos -> Vec2(8, 4 + pos.x) }))

            add(Rule(cubeSide = 3, direction = Direction.Right, newDirection = Direction.Down, transformPosition = { pos -> Vec2(15 - (pos.y - 4), 8) }))

            add(Rule(cubeSide = 4, direction = Direction.Left, newDirection = Direction.Down, transformPosition = { pos -> Vec2(4 + pos.y, 4) }))
            add(Rule(cubeSide = 4, direction = Direction.Up, newDirection = Direction.Down, transformPosition = { pos -> Vec2(3 - (pos.x - 8), 4) }))
            add(Rule(cubeSide = 4, direction = Direction.Right, newDirection = Direction.Left, transformPosition = { pos -> Vec2(15, 8 + (3 - pos.y)) }))

            add(Rule(cubeSide = 5, direction = Direction.Left, newDirection = Direction.Up, transformPosition = { pos -> Vec2(4 + (3 - (pos.y - 8)), 7) }))
            add(Rule(cubeSide = 5, direction = Direction.Down, newDirection = Direction.Up, transformPosition = { pos -> Vec2(3 - (pos.x - 8), 7) }))

            add(Rule(cubeSide = 6, direction = Direction.Up, newDirection = Direction.Left, transformPosition = { pos -> Vec2(11, 4 + (3 - (pos.x - 12))) }))
            add(Rule(cubeSide = 6, direction = Direction.Right, newDirection = Direction.Left, transformPosition = { pos -> Vec2(11, 3 - (pos.y - 8)) }))
            add(Rule(cubeSide = 6, direction = Direction.Down, newDirection = Direction.Right, transformPosition = { pos -> Vec2(0, 4 + (3 - (12 - pos.x))) }))
        } else {
            add(Rule(cubeSide = 1, direction = Direction.Up, newDirection = Direction.Up, transformPosition = { pos -> Vec2(pos.x - 100, 199) })) // -> 6
            add(Rule(cubeSide = 1, direction = Direction.Right, newDirection = Direction.Left, transformPosition = { pos -> Vec2(99, 100 + (49 - pos.y)) })) // -> 4
            add(Rule(cubeSide = 1, direction = Direction.Down, newDirection = Direction.Left, transformPosition = { pos -> Vec2(99, 50 + (pos.x - 100)) })) // -> 3

            add(Rule(cubeSide = 2, direction = Direction.Up, newDirection = Direction.Right, transformPosition = { pos -> Vec2(0, 150 + (pos.x - 50)) })) // -> 6
            add(Rule(cubeSide = 2, direction = Direction.Left, newDirection = Direction.Right, transformPosition = { pos -> Vec2(0, 149 - pos.y) })) // -> 5

            add(Rule(cubeSide = 3, direction = Direction.Right, newDirection = Direction.Up, transformPosition = { pos -> Vec2(100 + (pos.y - 50), 49) })) // -> 1
            add(Rule(cubeSide = 3, direction = Direction.Left, newDirection = Direction.Down, transformPosition = { pos -> Vec2(pos.y - 50, 100) })) // -> 5

            add(Rule(cubeSide = 4, direction = Direction.Right, newDirection = Direction.Left, transformPosition = { pos -> Vec2(149, 149 - pos.y) })) // -> 1
            add(Rule(cubeSide = 4, direction = Direction.Down, newDirection = Direction.Left, transformPosition = { pos -> Vec2(49, 150 + (pos.x - 50)) })) // -> 6

            add(Rule(cubeSide = 5, direction = Direction.Up, newDirection = Direction.Right, transformPosition = { pos -> Vec2(50, pos.x + 50) })) // -> 3
            add(Rule(cubeSide = 5, direction = Direction.Left, newDirection = Direction.Right, transformPosition = { pos -> Vec2(50, 149 - pos.y) })) // -> 2

            add(Rule(cubeSide = 6, direction = Direction.Left, newDirection = Direction.Down, transformPosition = { pos -> Vec2(50 + (pos.y - 150), 0) })) // -> 2
            add(Rule(cubeSide = 6, direction = Direction.Down, newDirection = Direction.Down, transformPosition = { pos -> Vec2(pos.x + 100, 0) })) // -> 1
            add(Rule(cubeSide = 6, direction = Direction.Right, newDirection = Direction.Up, transformPosition = { pos -> Vec2(50 + (pos.y - 150), 149) })) // -> 4
        }
    }

    return walkGrid(grid, instructions, cubeSides, rules)
}

private data class Rule(val cubeSide: Int, val direction: Direction, val newDirection: Direction, val transformPosition: (Vec2) -> Vec2)

private fun walkGrid(
    grid: Grid<Char>,
    instructions: List<Instruction>,
    cubeSides: Map<Vec2, Int> = emptyMap(),
    rules: List<Rule> = emptyList(),
): Int {
    val start = grid.getRowCells(0).first { (_, c) -> c == '.'}.first
    val state = instructions.fold(GridState(start, Direction.Right)) { state, instruction ->
        doInstruction(grid, state, instruction, cubeSides, rules)
    }

    return 1000 * (state.position.y + 1) + 4 * (state.position.x + 1) + state.direction.score
}

private fun doInstruction(
    grid: Grid<Char>,
    state: GridState,
    instruction: Instruction,
    cubeSides: Map<Vec2, Int>,
    rules: List<Rule>
): GridState {
    val ruleMap = rules.associate { (cubeSide, direction, newDirection, transform) ->
        (cubeSide to direction) to (newDirection to transform)
    }
    return when (instruction) {
        RotateRight -> state.copy(direction = Direction.values().first { it.ordinal == (state.direction.ordinal + 1) % 4 })
        RotateLeft -> state.copy(direction = Direction.values().first { it.ordinal == (state.direction.ordinal + 3) % 4 })
        is Move -> {
            var position = state.position
            var direction = state.direction
            repeat(instruction.amount) {
                var newPosition = position + direction.vector
                var newDirection = direction
                if (newPosition !in grid || grid[newPosition] == ' ') {
                    val cubeSide = cubeSides[position]
                    if (cubeSide == null) {
                        newPosition = when (direction) {
                            Direction.Right -> grid.getRowCells(newPosition.y).first { (_, c) -> c != ' ' }.first
                            Direction.Down -> grid.getColumnCells(newPosition.x).first { (_, c) -> c != ' ' }.first
                            Direction.Left -> grid.getRowCells(newPosition.y).last { (_, c) -> c != ' ' }.first
                            Direction.Up -> grid.getColumnCells(newPosition.x).last { (_, c) -> c != ' ' }.first
                        }
                    } else {
                        val (newDirectionFromRule, transform) = ruleMap.getValue(cubeSide to direction)
                        newDirection = newDirectionFromRule
                        newPosition = transform(position)
                    }
                }

                if (grid[newPosition] == '#') {
                    return@repeat
                }

                position = newPosition
                direction = newDirection
            }
            state.copy(position = position, direction = direction)
        }
    }
}

private fun parseGrid(input: List<String>): Grid<Char> {
    val width = input.maxOf { it.length }
    val height = input.size
    return Grid.fromInput(width, height) { (x, y) ->
        input.getOrNull(y)?.getOrNull(x) ?: ' '
    }
}

private fun parseInstructions(input: String): List<Instruction> {
    var currentMoveCount = 0
    return buildList {
        input.forEach { c ->
            if (c.isDigit()) {
                currentMoveCount = currentMoveCount * 10 + c.digitToInt()
                return@forEach
            }

            if (currentMoveCount != 0) {
                add(Move(currentMoveCount))
                currentMoveCount = 0
            }

            when (c) {
                'L' -> add(RotateLeft)
                'R' -> add(RotateRight)
                else -> error("xd")
            }

        }

        // last char is digit
        if (currentMoveCount != 0) {
            add(Move(currentMoveCount))
        }
    }
}

enum class Direction(val vector: Vec2, val score: Int) {
    Right(Vec2(1, 0), 0),
    Down(Vec2(0, 1), 1),
    Left(Vec2(-1, 0), 2),
    Up(Vec2(0, -1), 3),
}

sealed class Instruction
data class Move(val amount: Int): Instruction()
object RotateRight : Instruction()
object RotateLeft : Instruction()

data class GridState(val position: Vec2, val direction: Direction)