fun main() {

    data class Point2D(val x: Int, val y: Int)

    fun String.toPoint2D() = split(",").let { (x, y) -> Point2D(x.toInt(), y.toInt()) }

    data class Robot(
        val p: Point2D,
        val v: Point2D
    )

    fun List<String>.parseInput() = map { line ->
        val (p, v) = line.split(" ")
        Robot(
            p = p.substringAfter("p=").toPoint2D(),
            v = v.substringAfter("v=").toPoint2D()
        )
    }

    fun safetyFactor(newRobotPositions: List<Robot>, width: Int, height: Int): Int {
        val q1 = (0..<width / 2) to (0..<height / 2)
        val q2 = (width / 2 + 1..<width) to (0..<height / 2)
        val q3 = (0..<width / 2) to (height / 2 + 1..<height)
        val q4 = (width / 2 + 1..<width) to (height / 2 + 1..<height)

        val q1Count = newRobotPositions.count { it.p.x in q1.first && it.p.y in q1.second }
        val q2Count = newRobotPositions.count { it.p.x in q2.first && it.p.y in q2.second }
        val q3Count = newRobotPositions.count { it.p.x in q3.first && it.p.y in q3.second }
        val q4Count = newRobotPositions.count { it.p.x in q4.first && it.p.y in q4.second }

        return q1Count * q2Count * q3Count * q4Count
    }

    fun part1(input: List<Robot>, width: Int, height: Int): Int {
        val elapsedSeconds = 100
        val newRobotPositions = input.map { robot ->
            /*robot.copy(
                p = Point2D(
                    x = (robot.p.x + robot.v.x * elapsedSeconds)
                        .let { if (it < 0) it + width * elapsedSeconds else it } % width,
                    y = (robot.p.y + robot.v.y * elapsedSeconds)
                        .let { if (it < 0) it + height * elapsedSeconds else it } % height
                )
            )*/

            robot.copy(
                p = Point2D(
                    x = (robot.p.x + robot.v.x * elapsedSeconds).mod(width),
                    y = (robot.p.y + robot.v.y * elapsedSeconds).mod(height)
                )
            )
        }

        return safetyFactor(newRobotPositions, width, height)
    }

    fun List<Robot>.print(width: Int, height: Int) {
        val robotMap = map { it.p }.groupingBy { it }.eachCount()
        for (y in 0..<height) {
            for (x in 0..<width) {
                if (robotMap.contains(Point2D(x, y))) {
                    print('#')
                } else {
                    print(".")
                }
            }
            println("")
        }
    }

    fun part2(input: List<Robot>, width: Int, height: Int): Int {
        val positions =
            (1..(101 * 103)).runningFold(
                Triple(
                    0,
                    input,
                    safetyFactor(input, width, height)
                )
            ) { (_, robots, _), second ->
                val newRobots = robots.map { robot ->
                    robot.copy(
                        p = Point2D(
                            x = (robot.p.x + robot.v.x).mod(width),
                            y = (robot.p.y + robot.v.y).mod(height)
                        )
                    )
                }
                Triple(second, newRobots, safetyFactor(newRobots, width, height))
            }

        val solution = positions.sortedBy { it.third }.first()

        solution.second.print(width, height)

        return solution.first
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
    """.trimIndent().lines().parseInput()
    check(part1(testInput, 11, 7) == 12)
    //check(part2(testInput) == 1)

    // Or read a large test input from the `src/Day14_test.txt` file:
    //val testInput = readInput("Day14_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day14.txt` file.
    val input = readInput("Day14").parseInput()
    part1(input, 101, 103).println()
    part2(input, 101, 103).println()
}
