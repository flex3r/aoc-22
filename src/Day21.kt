suspend fun main() {
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    measureAndPrintResult {
        part1(input)
    }
    measureAndPrintResult {
        part2(input)
    }
}

private fun part1(input: List<String>): Long {
    val root = parseJob("root", input)
    return root.computed!!
}

private fun part2(input: List<String>): Long {
    val root = parseJob("root", input, checkForHuman = true) as Operation
    val computedA = root.childA.computed
    val computedB = root.childB.computed
    return when {
        computedA != null -> root.childB.computeInverse(computedA)
        computedB != null -> root.childA.computeInverse(computedB)
        else -> error("xd")
    }
}

private fun parseJob(name: String, input: List<String>, checkForHuman: Boolean = false): Job {
    val job = input.first { it.startsWith(name) }.substringAfter(": ")
    return when {
        job.first().isDigit() -> Constant(number = job.toLong().takeUnless { checkForHuman && name == "humn" })
        else -> {
            val (jobAName, operand, jobBName) = job.split(" ")
            val jobA = parseJob(jobAName, input, checkForHuman)
            val jobB = parseJob(jobBName, input, checkForHuman)
            Operation(op = operand, childA = jobA, childB = jobB)
        }
    }
}

private sealed class Job {
    val computed: Long? by lazy {
        when (this) {
            is Constant -> number
            is Operation -> {
                val computedA = childA.computed ?: return@lazy null
                val computedB = childB.computed ?: return@lazy null
                when (op) {
                    "+" -> computedA + computedB
                    "-" -> computedA - computedB
                    "*" -> computedA * computedB
                    "/" -> computedA / computedB
                    else -> error("xd")
                }
            }
        }
    }

    fun computeInverse(expected: Long): Long = when (this) {
        is Constant -> number ?: expected
        is Operation -> {
            val computedA = childA.computed
            val computedB = childB.computed
            val (unknown, other, isLeftUnknown) = when {
                computedA != null -> Triple(childB, computedA, false)
                computedB != null -> Triple(childA, computedB, true)
                else -> error("xd")
            }

            val newExpected = when (op) {
                "+" -> expected - other
                "-" -> if (isLeftUnknown) expected + other else other - expected
                "*" -> expected / other
                "/" -> if (isLeftUnknown) expected * other else other / expected
                else -> error("xd")
            }
            unknown.computeInverse(newExpected)
        }
    }
}

private data class Constant(val number: Long?) : Job()
private data class Operation(val op: String, val childA: Job, val childB: Job) : Job()