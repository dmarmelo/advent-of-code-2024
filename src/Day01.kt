import kotlin.math.abs

fun main() {

    fun List<String>.parseInput() = map { line ->
        val (first, second) = line.split("   ")
        first.toInt() to second.toInt()
    }.unzip()

    fun part1(input: Pair<List<Int>, List<Int>>): Int {
        return input.first.sorted().zip(input.second.sorted())
            .sumOf { abs(it.second - it.first) }
    }

    fun part2(input: Pair<List<Int>, List<Int>>): Int {
        val frequencies = input.second.groupingBy { it }.eachCount()
        return input.first.sumOf { it * (frequencies[it] ?: 0) }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Or read a large test input from the `src/Day01_test.txt` file:
    //val testInput = readInput("Day01_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01").parseInput()
    part1(input).println()
    part2(input).println()
}
