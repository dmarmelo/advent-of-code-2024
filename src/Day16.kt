import java.util.*
import kotlin.collections.mutableListOf

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

    data class Move(
        val position: Point2D,
        val facing: Direction,
        val cost: Int = 0
    )

    fun Move.step() = copy(position = position + facing, cost = 1)
    fun Move.turnLeft() = copy(facing = facing.turnLeft90, cost = 1000)
    fun Move.turnRight() = copy(facing = facing.turnRight90, cost = 1000)

    fun <T> dijkstra(
        start: T,
        isGoal: (T) -> Boolean,
        getNeighbors: (T) -> Iterable<T>,
        canMove: (from: T, to: T) -> Boolean = { _, _ -> true },
        calculateCost: (T) -> Int = { _ -> 1 }
    ): Int {
        val dist = mutableMapOf(start to 0)
        val queue = PriorityQueue<IndexedValue<T>> { n1, n2 -> n1.index - n2.index }
        queue.add(IndexedValue(dist[start]!!, start))
        while (queue.isNotEmpty()) {
            val (cost, item) = queue.poll()
            if (isGoal(item)) {
                return cost
            }
            getNeighbors(item)
                .filter { canMove(item, it) }
                .forEach {
                    val oldCost = dist[it] ?: Int.MAX_VALUE
                    val newCost = cost + calculateCost(it)
                    if (newCost < oldCost) {
                        dist[it] = newCost
                        queue.remove(IndexedValue(oldCost, it))
                        queue.add(IndexedValue(newCost, it))
                    }
                }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val inputJoined = input.joinToString("")
        val startIndex = inputJoined.indexOf('S')
        val endIndex = inputJoined.indexOf('E')

        val start = Move(
            position = Point2D(
                row = startIndex / input.first().length,
                column = startIndex % input.first().length
            ),
            facing = Direction.RIGHT
        )
        val endPosition = Point2D(
            row = endIndex / input.first().length,
            column = endIndex % input.first().length
        )

        return dijkstra(
            start = start,
            isGoal = { it.position == endPosition },
            getNeighbors = { listOf(it.step(), it.turnLeft(), it.turnRight()) },
            canMove = { _, to -> input[to.position.row][to.position.column] != '#' },
            calculateCost = { it.cost }
        )
    }

    fun <T> dijkstraAllPaths(
        start: T,
        isGoal: (T) -> Boolean,
        getNeighbors: (T) -> Iterable<T>,
        canMove: (from: T, to: T) -> Boolean = { _, _ -> true },
        calculateCost: (T) -> Int = { _ -> 1 }
    ): List<Pair<List<T>, Int>> {
        val dist = mutableMapOf(start to 0)
        val prev = mutableMapOf<T, MutableList<T>>()
        val queue = PriorityQueue<IndexedValue<T>> { n1, n2 -> n1.index - n2.index }
        queue.add(IndexedValue(dist[start]!!, start))

        val pathsWithCosts = mutableListOf<Pair<List<T>, Int>>()

        fun buildPaths(target: T): List<List<T>> {
            val pathsToCurrent = prev[target]?.flatMap { buildPaths(it) } ?: listOf(emptyList())
            return pathsToCurrent.map { it + target }
        }

        while (queue.isNotEmpty()) {
            val (cost, item) = queue.poll()
            if (isGoal(item)) {
                buildPaths(item).forEach { path ->
                    pathsWithCosts.add(path to cost)
                }
                return pathsWithCosts
            }
            getNeighbors(item)
                .filter { canMove(item, it) }
                .forEach {
                    val oldCost = dist[it] ?: Int.MAX_VALUE
                    val newCost = cost + calculateCost(it)
                    if (newCost < oldCost) {
                        dist[it] = newCost
                        prev[it] = mutableListOf(item)
                        queue.add(IndexedValue(newCost, it))
                    } else if (newCost == oldCost) {
                        prev[it]?.add(item)
                    }
                }
        }
        return pathsWithCosts
    }

    fun part2(input: List<String>): Int {
        val inputJoined = input.joinToString("")
        val startIndex = inputJoined.indexOf('S')
        val endIndex = inputJoined.indexOf('E')

        val start = Move(
            position = Point2D(
                row = startIndex / input.first().length,
                column = startIndex % input.first().length
            ),
            facing = Direction.RIGHT
        )
        val endPosition = Point2D(
            row = endIndex / input.first().length,
            column = endIndex % input.first().length
        )

        return dijkstraAllPaths(
            start = start,
            isGoal = { it.position == endPosition },
            getNeighbors = { listOf(it.step(), it.turnLeft(), it.turnRight()) },
            canMove = { _, to -> input[to.position.row][to.position.column] != '#' },
            calculateCost = { it.cost }
        ).map { it.first }.flatten()
            .map { it.position }.toHashSet().size
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        ###############
        #.......#....E#
        #.#.###.#.###.#
        #.....#.#...#.#
        #.###.#####.#.#
        #.#.#.......#.#
        #.#.#####.###.#
        #...........#.#
        ###.#.#####.#.#
        #...#.....#.#.#
        #.#.#.###.#.#.#
        #.....#...#.#.#
        #.###.#.#.#.#.#
        #S..#.....#...#
        ###############
    """.trimIndent().lines()
    check(part1(testInput) == 7036)
    check(part2(testInput) == 45)

    val testInput2 = """
        #################
        #...#...#...#..E#
        #.#.#.#.#.#.#.#.#
        #.#.#.#...#...#.#
        #.#.#.#.###.#.#.#
        #...#.#.#.....#.#
        #.#.#.#.#.#####.#
        #.#...#.#.#.....#
        #.#.#####.#.###.#
        #.#.#.......#...#
        #.#.###.#####.###
        #.#.#...#.....#.#
        #.#.#.#####.###.#
        #.#.#.........#.#
        #.#.#.#########.#
        #S#.............#
        #################
    """.trimIndent().lines()
    check(part1(testInput2) == 11048)
    check(part2(testInput2) == 64)

    // Or read a large test input from the `src/Day16_test.txt` file:
    //val testInput = readInput("Day16_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day16.txt` file.
    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
