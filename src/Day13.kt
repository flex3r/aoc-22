fun main() {
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.partitionBy { it.isEmpty() }
        .asSequence()
        .map { (a, b) -> parse(a).compareTo(parse(b)) }
        .withIndex()
        .filter { (_, res) -> res == -1 }
        .sumOf { (idx, _) -> idx + 1 }
}

private fun part2(input: List<String>): Int {
    val firstDivider = PacketList(listOf(PacketList(listOf(PacketValue(2)))))
    val secondDivider = PacketList(listOf(PacketList(listOf(PacketValue(6)))))
    val sorted = input
        .asSequence()
        .filterNot { it.isEmpty() }
        .map { parse(it) }
        .plus(firstDivider)
        .plus(secondDivider)
        .sorted()
        .toList()
    return (sorted.indexOf(firstDivider) + 1) * (sorted.indexOf(secondDivider) + 1)
}

private fun parse(value: String): Packet {
    if (value.firstOrNull()?.isDigit() == true) return PacketValue(value.toInt())
    return PacketList(
        packets = buildList {
            var openBrackets = 0
            var lastComma = 0
            value.forEachIndexed { idx, c ->
                when {
                    c == '[' -> openBrackets++
                    c == ']' -> if (--openBrackets == 0) add(parse(value.substring(lastComma + 1 until idx)))
                    c == ',' && openBrackets == 1 -> add(parse(value.substring(lastComma + 1 until idx))).also { lastComma = idx }
                }
            }
        }
    )
}

private sealed class Packet : Comparable<Packet> {
    override fun compareTo(other: Packet) = when {
        this is PacketValue && other is PacketValue -> this.packet.compareTo(other.packet)
        else -> compareLists(this.asList(), other.asList())
    }

    private fun compareLists(a: PacketList, b: PacketList): Int {
        a.packets.zip(b.packets).forEach { (a, b) ->
            val compared = a.compareTo(b)
            if (compared != 0) return compared
        }
        return a.packets.size.compareTo(b.packets.size)
    }

    private fun asList() = if (this is PacketList) this else PacketList(listOf(this))
}

private data class PacketList(val packets: List<Packet>) : Packet()
private data class PacketValue(val packet: Int) : Packet()

