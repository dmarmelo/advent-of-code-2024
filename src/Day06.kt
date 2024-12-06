import kotlin.system.measureTimeMillis

fun main() {

    data class Point2D(val row: Int, val column: Int)

    operator fun Point2D.plus(direction: Direction): Point2D {
        return when (direction) {
            Direction.LEFT -> copy(column = column - 1)
            Direction.RIGHT -> copy(column = column + 1)
            Direction.UP -> copy(row = row - 1)
            Direction.DOWN -> copy(row = row + 1)
        }
    }

    fun Point2D.isWithinBounds(rowRange: IntRange, columnRange: IntRange) =
        row in rowRange && column in columnRange

    fun part1(input: List<String>): Int {
        val rowRange = 0..input.size - 1
        val columnRange = 0..input.first().length - 1

        lateinit var start: Point2D
        for ((row, line) in input.withIndex()) {
            var col = line.indexOf('^')
            if (col >= 0) {
                start = Point2D(row, col)
            }
        }

        val visited = mutableSetOf<Point2D>(start)

        var current = start
        var direction = Direction.UP
        while (true) {
            val next = current + direction
            if (!next.isWithinBounds(rowRange, columnRange)) {
                break
            }

            if (input[next.row][next.column] == '#') {
                direction = direction.turnRight90
            } else {
                visited.add(next)
                current = next
            }
        }

        return visited.size
    }

    fun part2(input: List<String>): Int {
        val rowRange = 0..input.size - 1
        val columnRange = 0..input.first().length - 1

        lateinit var start: Point2D
        for ((row, line) in input.withIndex()) {
            var col = line.indexOf('^')
            if (col >= 0) {
                start = Point2D(row, col)
            }
        }

        val possibleNewObstacles = input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char -> if (char == '.') Point2D(row, col) else null }
        }

        return possibleNewObstacles.mapIndexed { index, newObstacle ->
            println("Run ${index + 1}/${possibleNewObstacles.size}")

            var current = start
            var direction = Direction.UP
            val visited = mutableSetOf<Pair<Point2D, Direction>>(current to direction)
            while (true) {
                val next = current + direction
                if (!next.isWithinBounds(rowRange, columnRange)) {
                    break
                }

                if (input[next.row][next.column] == '#' || next == newObstacle) {
                    direction = direction.turnRight90
                } else {
                    if (visited.contains(next to direction)) {
                        println("Found Loop!")
                        return@mapIndexed true
                    }
                    visited.add(next to direction)
                    current = next
                }
            }

            return@mapIndexed false
        }.count { it }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
    """.trimIndent().lines()
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    // Or read a large test input from the `src/Day06_test.txt` file:
    //val testInput = readInput("Day06_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day06.txt` file.
    val input = readInput("Day06")
    part1(input).println()
    measureTimeMillis {
        part2(input).println()
    }.also { println("Completed in $it ms") }
}
