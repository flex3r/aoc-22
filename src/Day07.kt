import kotlin.math.min

fun main() {
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val rootNode = buildTree(input)
    return sumDirectories(rootNode)
}

private fun part2(input: List<String>): Int {
    val rootNode = buildTree(input)
    val missingSpace = 30_000_000 - (70_000_000 - rootNode.totalSize)
    return findMinimumRequiredDirectorySize(rootNode, missingSpace)
}

private fun sumDirectories(node: Directory): Int {
    val dirSize = node.totalSize.takeIf { it < 100_000 } ?: 0
    return dirSize + node.directories.sumOf(::sumDirectories)
}

private fun findMinimumRequiredDirectorySize(node: Directory, missingSpace: Int): Int {
    val dirSize = node.totalSize.takeIf { it >= missingSpace } ?: Int.MAX_VALUE
    val subDirSize = node.directories
        .ifEmpty { return dirSize }
        .minOf { findMinimumRequiredDirectorySize(it, missingSpace) }
    return min(dirSize, subDirSize)
}

private fun buildTree(input: List<String>): Directory {
    val root = Directory(name = "/", parent = null)
    var currentDir = root
    input.forEach { line ->
        val parts = line.split(" ")
        when (parts[0]) {
            "$" -> when (parts[1]) {
                "ls" -> Unit
                "cd" -> currentDir = when (parts[2]) {
                    "/" -> root
                    ".." -> currentDir.parent ?: root
                    else -> currentDir.directories.first { it.name == parts[2] }
                }
            }

            "dir" -> currentDir.children += Directory(name = parts[1], parent = currentDir)
            else -> currentDir.children += File(name = parts[1], size = parts[0].toInt(), parent = currentDir)
        }
    }
    return root
}

private sealed class Node(open val name: String, open val parent: Directory?) {
    val totalSize: Int by lazy {
        when (this) {
            is File -> size
            is Directory -> children.sumOf(Node::totalSize)
        }
    }
}

private data class File(override val name: String, override val parent: Directory?, val size: Int) : Node(name, parent)
private data class Directory(override val name: String, override val parent: Directory?, val children: MutableList<Node> = mutableListOf()) : Node(name, parent) {
    val directories by lazy { children.filterIsInstance<Directory>() }
}