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

    fun <T> breathFirstSearchFull(
        start: T,
        isGoal: (T) -> Boolean,
        getNeighbors: (T) -> Iterable<T>,
        canMove: (from: T, to: T) -> Boolean = { _, _ -> true },
        keepTrackOfSeen: Boolean = true
    ): Int {
        val seen = mutableSetOf<T>()
        val queue = ArrayDeque<T>().apply { add(start) }
        var goalCounter = 0
        while (queue.isNotEmpty()) {
            val item = queue.removeFirst()
            if (isGoal(item)) {
                goalCounter++
            }
            getNeighbors(item)
                .filter { canMove(item, it) }
                .filter { !keepTrackOfSeen || it !in seen }
                .forEach {
                    if (keepTrackOfSeen) {
                        seen += it
                    }
                    queue.add(it)
                }
        }
        return goalCounter
    }

    fun List<String>.parseInput() = map { line ->
        line.map { it.digitToInt() }
    }

    fun solve(input: List<List<Int>>, keepTrackOfSeen: Boolean = true): Int {
        val startPoints = input.flatMapIndexed { rowIndex, line ->
            line.mapIndexedNotNull { columnIndex, value ->
                if (value == 0) Point2D(rowIndex, columnIndex)
                else null
            }
        }

        return startPoints.sumOf { start ->
            breathFirstSearchFull(
                start = start,
                isGoal = { input[it.row][it.column] == 9 },
                getNeighbors = {
                    listOf(
                        it.plus(Direction.UP),
                        it.plus(Direction.RIGHT),
                        it.plus(Direction.DOWN),
                        it.plus(Direction.LEFT)
                    ).filter { it.isWithinBounds(input.indices, input.first().indices) }
                },
                canMove = { from, to ->
                    input[to.row][to.column] - input[from.row][from.column] == 1
                },
                keepTrackOfSeen = keepTrackOfSeen
            )
        }
    }

    fun part1(input: List<List<Int>>): Int {
        return solve(input)
    }

    fun part2(input: List<List<Int>>): Int {
        return solve(input, false)
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    // Or read a large test input from the `src/Day10_test.txt` file:
    //val testInput = readInput("Day10_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day10.txt` file.
    val input = readInput("Day10").parseInput()
    part1(input).println()
    part2(input).println()
}
