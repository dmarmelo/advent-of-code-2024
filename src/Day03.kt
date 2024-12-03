fun main() {

    val mulRegex = """mul\((\d+),(\d+)\)""".toRegex()
    val doToken = "do()"
    val dontToken = "don't()"

    fun part1(input: List<String>): Int {
        return input.flatMap { line ->
            mulRegex.findAll(line).map {
                val (op1, op2) = it.destructured
                op1.toInt() * op2.toInt()
            }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.joinToString(separator = "")
            .split(doToken)
            .map { it.split(dontToken).first() }
            .flatMap { block ->
                mulRegex.findAll(block).map {
                    val (op1, op2) = it.destructured
                    op1.toInt() * op2.toInt()
                }
            }.sum()
    }

    // Test if implementation meets criteria from the description, like:
    val testInputPart1 = """
        xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
    """.trimIndent().lines()
    check(part1(testInputPart1) == 161)

    val testInputPart2 = """
        xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
    """.trimIndent().lines()
    check(part2(testInputPart2) == 48)

    // Or read a large test input from the `src/Day01_test.txt` file:
    //val testInput = readInput("Day01_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
