private enum class Tile {
    EMPTY, WALL, BOX, BOX_LEFT, BOX_RIGHT, ROBOT
}

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

    data class Input(
        val map: List<List<Tile>>,
        val movements: List<Direction>
    )

    fun Char.toTile() = when (this) {
        '.' -> Tile.EMPTY
        '#' -> Tile.WALL
        'O' -> Tile.BOX
        '@' -> Tile.ROBOT
        else -> error("Can't convert $this to tile")
    }

    fun Tile.toChar() = when (this) {
        Tile.EMPTY -> '.'
        Tile.WALL -> '#'
        Tile.BOX -> 'O'
        Tile.BOX_LEFT -> '['
        Tile.BOX_RIGHT -> ']'
        Tile.ROBOT -> '@'
    }

    fun Char.toDirection() = when (this) {
        '<' -> Direction.LEFT
        '>' -> Direction.RIGHT
        '^' -> Direction.UP
        'v' -> Direction.DOWN
        else -> error("Can't convert $this to direction")
    }

    fun List<String>.parseInput(): Input {
        val map = takeWhile { !it.isBlank() }
        val movements = takeLastWhile { !it.isBlank() }
        return Input(
            map = map.map { line -> line.map { it.toTile() } },
            movements = movements.flatMap { line -> line.map { it.toDirection() } }
        )
    }

    fun List<List<Tile>>.push(position: Point2D, direction: Direction): List<Pair<Point2D, Point2D>> {
        val safePushes = mutableListOf<Pair<Point2D, Point2D>>()
        val seen = mutableSetOf<Point2D>()
        val queue = ArrayDeque<Point2D>().apply { add(position) }
        while (queue.isNotEmpty()) {
            val pos = queue.removeFirst()
            if (pos !in seen) {
                seen += pos
                if (direction in setOf(Direction.UP, Direction.DOWN)) {
                    val tile = this[pos.row][pos.column]
                    when (tile) {
                        Tile.BOX_LEFT -> queue.add(pos + Direction.RIGHT)
                        Tile.BOX_RIGHT -> queue.add(pos + Direction.LEFT)
                        else -> {}
                    }
                }
                val nextPos = pos + direction
                val nextTile = this[nextPos.row][nextPos.column]
                when (nextTile) {
                    Tile.WALL -> return emptyList()
                    Tile.BOX, Tile.BOX_LEFT, Tile.BOX_RIGHT -> queue.add(nextPos)
                    else -> {}
                }
                safePushes.add(pos to nextPos)
            }
        }
        return safePushes.reversed()
    }

    fun List<List<Tile>>.print() = map { row ->
        row.map { it.toChar() }.joinToString(separator = "")
    }.forEach(::println)

    fun solve(input: Input): Int {
        val robotStartIndex = input.map.flatten().indexOfFirst { it == Tile.ROBOT }
        var robotPosition = Point2D(
            row = robotStartIndex / input.map.first().size,
            column = robotStartIndex % input.map.size
        )

        val warehouse = input.map.map { it.toMutableList() }

        //warehouse.print()

        input.movements.forEach { dir ->
            val nextPosition = robotPosition + dir
            val nextTile = warehouse[nextPosition.row][nextPosition.column]

            var moved = false
            if (nextTile in listOf(Tile.BOX, Tile.BOX_LEFT, Tile.BOX_RIGHT)) {
                val boxesToPush = warehouse.push(nextPosition, dir)
                if (boxesToPush.isNotEmpty()) {
                    boxesToPush.forEach { (from, to) ->
                        warehouse[to.row][to.column] = warehouse[from.row][from.column]
                        warehouse[from.row][from.column] = Tile.EMPTY
                    }
                    moved = true
                }
            } else if (nextTile != Tile.WALL) {
                moved = true
            }

            if (moved) {
                warehouse[nextPosition.row][nextPosition.column] = warehouse[robotPosition.row][robotPosition.column]
                warehouse[robotPosition.row][robotPosition.column] = Tile.EMPTY
                robotPosition = nextPosition
            }
        }

        //warehouse.print()

        return warehouse.flatten().mapIndexedNotNull { index, tile ->
            when (tile) {
                Tile.BOX, Tile.BOX_LEFT -> 100 * (index / warehouse.first().size) + (index % warehouse.first().size)
                else -> null
            }
        }.sum()
    }

    fun part1(input: Input): Int {
        return solve(input)
    }

    fun Input.remap() = copy(
        map = map.map { row ->
            row.flatMap {
                when (it) {
                    Tile.EMPTY -> List(2) { Tile.EMPTY }
                    Tile.WALL -> List(2) { Tile.WALL }
                    Tile.BOX -> listOf(Tile.BOX_LEFT, Tile.BOX_RIGHT)
                    Tile.ROBOT -> listOf(Tile.ROBOT, Tile.EMPTY)
                    else -> error("Can't remap $it")
                }
            }
        }
    )

    fun part2(input: Input): Int {
        return solve(input.remap())
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 10092)
    check(part2(testInput) == 9021)

    // Or read a large test input from the `src/Day15_test.txt` file:
    //val testInput = readInput("Day15_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day15.txt` file.
    val input = readInput("Day15").parseInput()
    part1(input).println()
    part2(input).println()
}
