import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.max

suspend fun main() {
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 3348)

    val input = readInput("Day19")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private suspend fun part1(input: List<String>) = coroutineScope {
    parseBlueprints(input)
        .withIndex()
        .map { (i, it) -> async { buildAndCollect(it, maxTime = 24) * (i + 1) } }
        .awaitAll()
        .sum()
}

private suspend fun part2(input: List<String>) = coroutineScope {
    parseBlueprints(input)
        .take(3)
        .map { async { buildAndCollect(it, maxTime = 32) } }
        .awaitAll()
        .product()
}

private fun buildAndCollect(blueprint: Blueprint, maxTime: Int): Int {
    val (oreBot, clayBot, obsidianBot, geodeBot) = blueprint
    val queue = ArrayDeque(listOf(State(0, Inventory())))
    val seen = mutableSetOf<Inventory>()
    var geodes = 0
    while (queue.isNotEmpty()) {
        val (time, inv) = queue.removeFirst()
        if (!seen.add(inv)) {
            continue
        }
        if (time == maxTime) {
            geodes = max(geodes, inv.geode)
            continue
        }

        // always build a geode bot if we can, don't build anything else
        if (inv.ore >= geodeBot.oreCost && inv.obsidian >= geodeBot.obsidianCost) {
            val newInv = inv.collect().buildRobot(geodeBot).copy(geodeBot = inv.geodeBot + 1)
            queue.addLast(State(time + 1, newInv))
            continue
        }

        // build obsidian bot if we still need one, don't build anything else
        if (inv.obsidianBot < blueprint.maxObsidianCost && inv.ore >= obsidianBot.oreCost && inv.clay >= obsidianBot.clayCost) {
            val newInv = inv.collect().buildRobot(obsidianBot).copy(obsidianBot = inv.obsidianBot + 1)
            queue.addLast(State(time + 1, newInv))
            continue
        }

        if (inv.oreBot < blueprint.maxOreCost && inv.ore >= oreBot.oreCost) {
            val newInv = inv.collect().buildRobot(oreBot).copy(oreBot = inv.oreBot + 1)
            queue.addLast(State(time + 1, newInv))
        }

        if (inv.clayBot < blueprint.maxClayCost && inv.ore >= clayBot.oreCost) {
            val newInv = inv.collect().buildRobot(clayBot).copy(clayBot = inv.clayBot + 1)
            queue.addLast(State(time + 1, newInv))
        }

        queue.addLast(State(time + 1, inv.collect()))
    }

    return geodes
}

private fun parseBlueprints(input: List<String>): List<Blueprint> {
    return input.map { line ->
        val parts = line.split(" ")
        val oreBot = Recipe(oreCost = parts[6].toInt())
        val clayBot = Recipe(oreCost = parts[12].toInt())
        val obsidianBot = Recipe(oreCost = parts[18].toInt(), clayCost = parts[21].toInt())
        val geodeBot = Recipe(oreCost = parts[27].toInt(), obsidianCost = parts[30].toInt())
        Blueprint(oreBot, clayBot, obsidianBot, geodeBot)
    }
}
private data class State(val time: Int, val inventory: Inventory)
private data class Inventory(
    val ore: Int = 0,
    val oreBot: Int = 1,
    val clay: Int = 0,
    val clayBot: Int = 0,
    val obsidian: Int = 0,
    val obsidianBot: Int = 0,
    val geode: Int = 0,
    val geodeBot: Int = 0,
) {
    fun collect(): Inventory = copy(
        ore = ore + oreBot,
        clay = clay + clayBot,
        obsidian = obsidian + obsidianBot,
        geode = geode + geodeBot,
    )
    fun buildRobot(recipe: Recipe): Inventory = copy(
        ore = ore - recipe.oreCost,
        clay = clay - recipe.clayCost,
        obsidian = obsidian - recipe.obsidianCost
    )
}
private data class Blueprint(val oreBot: Recipe, val clayBot: Recipe, val obsidianBot: Recipe, val geodeBot: Recipe) {
    val maxOreCost = listOf(oreBot.oreCost, clayBot.oreCost, obsidianBot.oreCost, geodeBot.oreCost).max()
    val maxClayCost = obsidianBot.clayCost
    val maxObsidianCost = geodeBot.obsidianCost
}
private data class Recipe(val oreCost: Int = 0, val clayCost: Int = 0, val obsidianCost: Int = 0)