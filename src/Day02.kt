fun main() {

    fun List<String>.parseInput() = map { it.split(" ").map { it.toInt() } }

    fun List<Int>.isSafe(): Boolean {
        val diffs = zipWithNext().map { it.second - it.first }
        return diffs.all { it > 0 && it <= 3 } || diffs.all { it < 0 && it >= -3 }
    }

    fun part1(input: List<List<Int>>): Int {
        return input.count { it.isSafe() }
    }

    fun List<Int>.toleratedLevels() = sequence<List<Int>> {
        repeat(size) { index ->
            yield(take(index) + drop(index + 1))
        }
    }

    fun part2(input: List<List<Int>>): Int {
        return input.count { it.isSafe() || it.toleratedLevels().any { it.isSafe() } }
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

    // Or read a large test input from the `src/Day01_test.txt` file:
    //val testInput = readInput("Day01_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02").parseInput()
    part1(input).println()
    part2(input).println()
}
