import kotlin.collections.indices

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

    fun <T> search(
        start: T,
        getNeighbors: (T) -> Iterable<T>,
        canMove: (from: T, to: T) -> Boolean = { _, _ -> true }
    ): List<T> {
        val visited = mutableSetOf<T>(start)
        val queue = ArrayDeque<T>().apply { add(start) }
        while (queue.isNotEmpty()) {
            val item = queue.removeFirst()
            getNeighbors(item)
                .filter { canMove(item, it) }
                .filter { it !in visited }
                .forEach {
                    visited += it
                    queue.add(it)
                }
        }
        return visited.toList()
    }

    fun part1(input: List<String>): Int {
        val map = input.map { line -> line.map { it } }
            .flatten()
            .mapIndexed { index, c ->
                Point2D(row = index / input.size, column = index % input.first().length) to c
            }.toMap()

        var notVisited = map.keys.toMutableSet()
        val groups = mutableListOf<Pair<Char,List<Point2D>>>()
        while (notVisited.isNotEmpty()) {
            val first = notVisited.first()
            val group = search(
                start = first,
                getNeighbors = {
                    Direction.entries.map { dir -> it + dir }
                        .filter { it.isWithinBounds(input.indices, input.first().indices) }
                },
                canMove = { a, b ->
                    map.getValue(a) == map.getValue(b)
                }
            )

            groups.add(map.getValue(first) to group)
            notVisited.removeAll(group)
        }

        return groups.sumOf { (id, group) ->
            val area = group.size
            val perimeter = group.flatMap { point ->
                Direction.entries.map { dir -> point + dir }
                    .filter { !map.containsKey(it) || map.getValue(it) != id }
            }.size
            area * perimeter
        }
    }

    fun part2(input: List<String>): Int {
        val map = input.map { line -> line.map { it } }
            .flatten()
            .mapIndexed { index, c ->
                Point2D(row = index / input.size, column = index % input.first().length) to c
            }.toMap()

        var notVisited = map.keys.toMutableSet()
        val groups = mutableListOf<Pair<Char,List<Point2D>>>()
        while (notVisited.isNotEmpty()) {
            val first = notVisited.first()
            val group = search(
                start = first,
                getNeighbors = {
                    Direction.entries.map { dir -> it + dir }
                        .filter { it.isWithinBounds(input.indices, input.first().indices) }
                },
                canMove = { a, b ->
                    map.getValue(a) == map.getValue(b)
                }
            )

            groups.add(map.getValue(first) to group)
            notVisited.removeAll(group)
        }

        return groups.sumOf { (id, group) ->
            val area = group.size

            val perimeter = group.map { point ->
                val neighbors = Direction.entries.map { dir -> point + dir }

                val outside = neighbors.filter { !map.containsKey(it) || map.getValue(it) != id }.size
                //val inside = neighbors.filter { map.containsKey(it) && map.getValue(it) == id }.size

                // TODO check diagonals
                if (outside == 3 || outside == 1) 1
                else if (outside == 0) 4
                else 0
            }.sum()

            area * perimeter
        }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent().lines()
    check(part1(testInput) == 1930)
    check(part2(testInput) == 1206)

    // Or read a large test input from the `src/Day12_test.txt` file:
    //val testInput = readInput("Day12_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day12.txt` file.
    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
