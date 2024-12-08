fun main() {

    data class Point2D(val row: Int, val column: Int)

    operator fun Point2D.plus(other: Point2D) = Point2D(row + other.row, column + other.column)

    operator fun Point2D.minus(other: Point2D) = Point2D(row - other.row, column - other.column)

    data class AntenaMap(
        val rowRange: IntRange,
        val columnRange: IntRange,
        val antenasByFrequency: Map<String, List<Point2D>>
    )

    operator fun AntenaMap.contains(point: Point2D) =
        point.row in rowRange && point.column in columnRange

    fun List<String>.parseInput() = AntenaMap(
        rowRange = indices,
        columnRange = first().indices,
        antenasByFrequency = flatMapIndexed { rowIndex, line ->
            """\w|\d""".toRegex().findAll(line).map { it.value to Point2D(rowIndex, it.range.first) }
        }.groupBy({ it.first }) { it.second }
    )

    fun <T> combinations(list: List<T>, k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (list.isEmpty()) return emptyList()

        val head = list.first()
        val tail = list.drop(1)

        val withHead = combinations(tail, k - 1).map { it + head }
        val withoutHead = combinations(tail, k)

        return withHead + withoutHead
    }

    fun part1(input: AntenaMap): Int {
        return input.antenasByFrequency.flatMap { (frequency, antenas) ->
            combinations(antenas, 2).flatMap { (a, b) ->
                val distance = b - a
                listOf(a - distance, b + distance)
            }
        }.toSet().count { it in input }
    }

    fun part2(input: AntenaMap): Int {
        return input.antenasByFrequency.flatMap { (frequency, antenas) ->
            combinations(antenas, 2).flatMap { (a, b) ->
                val distance = b - a

                val antinodes = mutableListOf(a, b)

                var ar = a - distance
                while (ar in input) {
                    antinodes += ar
                    ar -= distance
                }

                var br = b + distance
                while (br in input) {
                    antinodes += br
                    br += distance
                }

                antinodes
            }
        }.toSet().size
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        ............
        ........0...
        .....0......
        .......0....
        ....0.......
        ......A.....
        ............
        ............
        ........A...
        .........A..
        ............
        ............
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    // Or read a large test input from the `src/Day08_test.txt` file:
    //val testInput = readInput("Day08_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day08.txt` file.
    val input = readInput("Day08").parseInput()
    part1(input).println()
    part2(input).println()
}
