fun main() {

    fun List<String>.parseInput() = map { it.split(" ").map { it.toInt() } }

    fun isReportSafe(levels: List<Int>) = levels.zipWithNext { a, b -> b - a }
        .let { slope -> slope.all { it in 1..3 } || slope.all { it in -3..-1 } }

    fun part1(input: List<List<Int>>): Int {
        return input.count(::isReportSafe)
    }

    fun isReportSafeDampened(levels: List<Int>) = levels.indices.any { index ->
        isReportSafe(levels.toMutableList().apply { removeAt(index) })
    }

    fun part2(input: List<List<Int>>): Int {
        return input.count { isReportSafe(it) || isReportSafeDampened(it) }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        7 6 4 2 1
        1 2 7 8 9
        9 7 6 2 1
        1 3 2 4 5
        8 6 4 4 1
        1 3 6 7 9
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Or read a large test input from the `src/Day02_test.txt` file:
    //val testInput = readInput("Day02_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day02.txt` file.
    val input = readInput("Day02").parseInput()
    part1(input).println()
    part2(input).println()
}
