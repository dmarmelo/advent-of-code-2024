fun main() {

    data class Coordinate(val row: Int, val col: Int)

    fun List<String>.getOrNull(row: Int, col: Int) = getOrNull(row)?.getOrNull(col)

    fun part1(input: List<String>): Int {
        val xmas = "XMAS"
        val indices = xmas.indices

        return input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c -> if (c == xmas.first()) Coordinate(row, column) else null }
        }.flatMap { x ->
            val up = indices.mapNotNull { input.getOrNull(x.row - it, x.col) }
            val upRight = indices.mapNotNull { input.getOrNull(x.row - it, x.col + it) }
            val right = indices.mapNotNull { input.getOrNull(x.row, x.col + it) }
            val downRight = indices.mapNotNull { input.getOrNull(x.row + it, x.col + it) }
            val down = indices.mapNotNull { input.getOrNull(x.row + it, x.col) }
            val downLeft = indices.mapNotNull { input.getOrNull(x.row + it, x.col - it) }
            val left = indices.mapNotNull { input.getOrNull(x.row, x.col - it) }
            val upLeft = indices.mapNotNull { input.getOrNull(x.row - it, x.col - it) }

            listOf(up, right, down, left, upRight, downRight, downLeft, upLeft).map { it.joinToString("") }
        }.count { it == xmas }
    }

    fun part2(input: List<String>): Int {
        val height = input.size - 1
        val width = input.first().length - 1

        val a = 'A'
        val ms = "MS".toSet()

        return input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c -> if (c == a) Coordinate(row, column) else null }
        }.filter { it.row in (1..<height) && it.col in (1..<width) }
            .count { a ->
                val left = setOf(input[a.row - 1][a.col - 1], input[a.row + 1][a.col + 1])
                val right = setOf(input[a.row - 1][a.col + 1], input[a.row + 1][a.col - 1])
                left == ms && right == ms
            }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent().lines()
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    // Or read a large test input from the `src/Day04_test.txt` file:
    //val testInput = readInput("Day04_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day04.txt` file.
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
