fun main() {

    data class PrizeLocation(val x: Long, val y: Long)

    data class ButtonDelta(val dx: Int, val dy: Int)

    data class ClawMachine(
        val buttonA: ButtonDelta,
        val buttonB: ButtonDelta,
        val prize: PrizeLocation
    )

    fun String.toButtonDelta() = substringAfter(": ").split(", ")
        .map { it.substring(2) }
        .let { (dx, dy) -> ButtonDelta(dx.toInt(), dy.toInt()) }

    fun String.toPrizeLocation() = substringAfter(": ").split(", ")
        .map { it.substring(2) }
        .let { (x, y) -> PrizeLocation(x.toLong(), y.toLong()) }

    fun String.parseInput() = split("\n\n")
        .map { machine ->
            val (buttonA, buttonB, prize) = machine.lines()
            ClawMachine(
                buttonA = buttonA.toButtonDelta(),
                buttonB = buttonB.toButtonDelta(),
                prize = prize.toPrizeLocation()
            )
        }


    fun part1(input: List<ClawMachine>): Long {
        // 8400 = 94a + 22b
        // 5400 = 34a + 67b

        // a = (8400 - 22b) / 94
        // b = (5400 - 34a) / 67

        // a = ((8400 * 67) - (22 * 5400)) / ((67 * 94) - (22 * 34))

        // a=80, b=40

        return input.sumOf { machine ->
            val aCount = ((machine.prize.x * machine.buttonB.dy) - (machine.prize.y * machine.buttonB.dx)) /
                    ((machine.buttonA.dx * machine.buttonB.dy) - (machine.buttonA.dy * machine.buttonB.dx))
            val bCount = (machine.prize.y - machine.buttonA.dy * aCount) / machine.buttonB.dy

            if (aCount >= 0 && aCount <= 100 && bCount >= 0 && bCount <= 100) {
                if (aCount * machine.buttonA.dx + bCount * machine.buttonB.dx != machine.prize.x ||
                    aCount * machine.buttonA.dy + bCount * machine.buttonB.dy != machine.prize.y
                ) {
                    return@sumOf 0
                }

                aCount * 3 + bCount
            }
            else 0
        }
    }

    fun part2(input: List<ClawMachine>): Long {
        val fixedInput = input.map {
            it.copy(
                prize = PrizeLocation(x = it.prize.x + 10000000000000, y = it.prize.y + 10000000000000)
            )
        }

        return fixedInput.sumOf { machine ->
            val aCount = ((machine.prize.x * machine.buttonB.dy) - (machine.prize.y * machine.buttonB.dx)) /
                    ((machine.buttonA.dx * machine.buttonB.dy) - (machine.buttonA.dy * machine.buttonB.dx))
            val bCount = (machine.prize.y - machine.buttonA.dy * aCount) / machine.buttonB.dy

            if (aCount >= 0 && bCount >= 0) {
                if (aCount * machine.buttonA.dx + bCount * machine.buttonB.dx != machine.prize.x ||
                    aCount * machine.buttonA.dy + bCount * machine.buttonB.dy != machine.prize.y
                ) {
                    return@sumOf 0
                }

                aCount * 3 + bCount
            }
            else 0
        }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent().parseInput()
    check(part1(testInput) == 480L)
    check(part2(testInput) == 875318608908L)

    // Or read a large test input from the `src/Day13_test.txt` file:
    //val testInput = readInput("Day13_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day13.txt` file.
    val input = readInputRaw("Day13").parseInput()
    part1(input).println()
    part2(input).println()
}
