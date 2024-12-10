import kotlin.system.measureTimeMillis

fun main() {

    fun part1(input: String): Long {
        val expandedMemory = input.flatMapIndexed { index, block ->
            if (index % 2 == 0) {
                // Occupied Memory
                List(block.digitToInt()) { index / 2 }
            } else {
                // Free Space
                List(block.digitToInt()) { -1 }
            }
        }

        val queue = ArrayDeque(expandedMemory)
        val defragmented = mutableListOf<Int>()
        while (queue.isNotEmpty()) {
            var slot = queue.removeFirst()
            if (queue.isNotEmpty() && slot < 0) {
                var last = queue.removeLast()
                while (last < 0) {
                    last = queue.removeLast()
                }
                slot = last
            }
            defragmented += slot
        }

        return defragmented.mapIndexed { index, slot -> index * slot.toLong() }.sum()
    }

    fun part2(input: String): Long {
        val expandedMemory = input.mapIndexed { index, block ->
            val blockCount = block.digitToInt()
            if (index % 2 == 0) {
                // Occupied Memory
                index / 2
            } else {
                // Free Space
                -1
            } to blockCount
        }

        //expandedMemory.println()

        val queue = ArrayDeque(expandedMemory)
        val visited = mutableSetOf<Pair<Int, Int>>()
        val defragmented = mutableListOf<Pair<Int, Int>>()

        val reversedMemory = expandedMemory.reversed()

        while (queue.isNotEmpty()) {
            var block = queue.removeFirst()
            if (block.first < 0) {
                val find = reversedMemory.find { pair ->
                    pair.first >= 0 &&
                            pair !in visited &&
                            pair.second <= block.second
                }
                if (find != null) {
                    val diff = block.second - find.second
                    defragmented += find
                    if (diff > 0) {
                        queue.addFirst(-1 to diff)
                    }
                    visited.add(find)
                } else {
                    defragmented += block
                }
            } else if (block in visited) {
                defragmented += -1 to block.second
            } else {
                visited.add(block)
                defragmented += block
            }
        }

        //defragmented.println()

        return defragmented.flatMap { (slot, count) ->
            List(count) {
                if (slot >= 0) slot
                else 0
            }
        }.mapIndexed { index, slot -> index * slot.toLong() }.sum()
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        2333133121414131402
    """.trimIndent()
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    // Or read a large test input from the `src/Day09_test.txt` file:
    //val testInput = readInput("Day09_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day09.txt` file.
    val input = readInputRaw("Day09")
    part1(input).println()
    measureTimeMillis {
        part2(input).println()
    }.also { println("Completed in $it ms") }
}
