import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>): Int {
    val (pathDistances, flowRates, tunnels) = parseTunnels(input)
    return solve("AA", tunnels, 0, 0, 0, pathDistances, flowRates, 30)
}

private suspend fun part2(input: List<String>): Int = coroutineScope {
    val (pathDistances, flowRates, tunnels) = parseTunnels(input)
    (1..tunnels.size / 2).maxOf { i ->
        combinations(tunnels, i).toList().map { tunnelsCombination ->
            async {
                val opposite = tunnels - tunnelsCombination.toSet()
                val ownScore = solve("AA", tunnelsCombination, 0, 0, 0, pathDistances, flowRates, 26)
                val elephantScore = solve("AA", opposite, 0, 0, 0, pathDistances, flowRates, 26)
                ownScore + elephantScore
            }
        }.awaitAll().max()
    }
}

private fun solve(
    tunnel: String,
    remainingTunnels: List<String>,
    time: Int,
    flow: Int,
    pressure: Int,
    pathDistances: Map<String, Map<String, Int>>,
    flowRates: Map<String, Int>,
    limit: Int,
): Int {
    // initial pressure for current time and flow
    var maxPressure = pressure + (limit - time) * flow
    remainingTunnels.forEach { newTunnel ->
        val travelAndOpenTime = pathDistances.getValue(tunnel).getValue(newTunnel) + 1
        if (time + travelAndOpenTime >= limit) return@forEach
        val newTime = time + travelAndOpenTime
        val newPressure = pressure + travelAndOpenTime * flow
        val newFlow = flow + flowRates.getValue(newTunnel)
        val newRemainingTunnels = remainingTunnels - newTunnel
        val newScore = solve(newTunnel, newRemainingTunnels, newTime, newFlow, newPressure, pathDistances, flowRates, limit)
        if (newScore > maxPressure) {
            maxPressure = newScore
        }
    }
    return maxPressure
}

private fun parseTunnels(input: List<String>): Triple<Map<String, Map<String, Int>>, Map<String, Int>, List<String>> {
    val flowRates = mutableMapOf<String, Int>()
    val paths = mutableMapOf<String, List<String>>()
    val tunnels = input.map {
        val (data, tunnels) = it.split(";")
        val name = data.substringAfter("Valve ").substringBefore(" has")
        val rate = data.substringAfter("rate=").toInt()
        val otherValves = tunnels.substringAfter("valves ", missingDelimiterValue = "")
            .ifEmpty { tunnels.substringAfter("valve ") }
            .split(", ")
        flowRates[name] = rate
        paths[name] = otherValves
        name
    }

    val shortestPathDistances = tunnels.associateWith { t1 ->
        tunnels.associateWith { t2 -> bfs(t1) { paths.getValue(it) }.first { it.value == t2 }.index }
    }
    // reduces tunnels from 59 to 15!!
    val tunnelsWithNonZeroRate = tunnels.filter { flowRates.getValue(it) > 0 }
    return Triple(shortestPathDistances, flowRates, tunnelsWithNonZeroRate)
}