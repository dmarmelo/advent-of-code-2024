fun main() {

    val mulRegex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

    fun findAllMulAndCompute(block: String) = mulRegex.findAll(block).map {
        val (op1, op2) = it.destructured
        op1.toInt() * op2.toInt()
    }

    fun part1(input: List<String>): Int {
        return input.flatMap(::findAllMulAndCompute).sum()
    }

    fun part2(input: List<String>): Int {
        return input.joinToString("")
            .split("do()")
            .map { it.split("don't()").first() }
            .flatMap(::findAllMulAndCompute)
            .sum()
    }

    fun part2Regex(input: List<String>): Int {
        val regex = """mul\((\d{1,3}),(\d{1,3})\)|do(n't)?\(\)""".toRegex()
        var sum = 0
        var enabled = true
        val instructions = regex.findAll(input.joinToString(""))
        for (match in instructions) {
            val instruction = match.value
            when {
                instruction == "do()" -> enabled = true
                instruction == "don't()" -> enabled = false
                enabled && instruction.startsWith("mul(") -> {
                    val (op1, op2) = match.destructured
                    sum += op1.toInt() * op2.toInt()
                }
            }
        }
        return sum
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
    check(part2Regex(testInputPart2) == 48)

    // Or read a large test input from the `src/Day03_test.txt` file:
    //val testInput = readInput("Day03_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day03.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
    part2Regex(input).println()
}
