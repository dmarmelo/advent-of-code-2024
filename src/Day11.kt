import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {

    fun String.parseInput() = split(" ").map { it.toLong() }

    fun blink(stone: Long): List<Long> {
        val digitCount = floor(log10(stone.toDouble()) + 1).roundToInt()
        return when {
            stone == 0L -> listOf(1L)
            digitCount % 2 == 0 -> {
                val mask = (10.0).pow(digitCount / 2).toInt()
                listOf(stone / mask, stone % mask)
            }

            else -> listOf(stone * 2024)
        }
    }

    fun solve(stones: List<Long>, blinks: Int): Long {
        var stonesCount = stones.groupingBy { it }.eachCount()
            .mapValues { it.value.toLong() }
            .toMutableMap()

        repeat(blinks) {
            val newStonesCount = mutableMapOf<Long, Long>()
            stonesCount.forEach { stone, count ->
                blink(stone).forEach { newStone ->
                    newStonesCount[newStone] = newStonesCount.getOrDefault(newStone, 0) + count
                }
            }
            stonesCount = newStonesCount
        }
        return stonesCount.values.sum()
    }

    fun part1(input: List<Long>): Long {
        return solve(input, 25)
    }

    fun part2(input: List<Long>): Long {
        return solve(input, 75)
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        125 17
    """.trimIndent().parseInput()
    check(part1(testInput) == 55312L)
    check(part2(testInput) == 65601038650482L)

    // Or read a large test input from the `src/Day11_test.txt` file:
    //val testInput = readInput("Day11_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day11.txt` file.
    val input = readInputRaw("Day11").parseInput()
    part1(input).println()
    part2(input).println()
}
