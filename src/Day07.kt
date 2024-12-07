fun main() {

    fun List<String>.parseInput() = map { line ->
        val (result, values) = line.split(": ")
        result.toLong() to values.split(" ").map { it.toLong() }
    }

    fun canMake(result: Long, values: List<Long>, concatenate: Boolean = false): Boolean {
        if (values.size == 1) {
            return values.first() == result
        }

        val last = values.last()

        val possibleMul = if (result % last == 0L)
            canMake(result / last, values.dropLast(1), concatenate)
        else false

        var possibleConcat = if (concatenate) {
            var nextPowerOf10 = 1
            while (nextPowerOf10 <= last) {
                nextPowerOf10 *= 10
            }
            if ((result - last) % nextPowerOf10 == 0L)
                canMake((result - last) / nextPowerOf10, values.dropLast(1), concatenate)
            else false
        } else false

        val possibleAdd = canMake(result - last, values.dropLast(1), concatenate)

        return possibleMul || possibleAdd || possibleConcat
    }

    fun solve(input: List<Pair<Long, List<Long>>>, concatenate: Boolean = false): Long {
        return input.map { (result, values) -> result to canMake(result, values, concatenate) }
            .filter { (_, possible) -> possible }
            .sumOf { (result, _) -> result }
    }

    fun part1(input: List<Pair<Long, List<Long>>>): Long {
        return solve(input)
    }

    fun part2(input: List<Pair<Long, List<Long>>>): Long {
        return solve(input, concatenate = true)
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    // Or read a large test input from the `src/Day07_test.txt` file:
    //val testInput = readInput("Day07_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day07.txt` file.
    val input = readInput("Day07").parseInput()
    part1(input).println()
    part2(input).println()
}
