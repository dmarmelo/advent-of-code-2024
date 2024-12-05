private typealias Page = Int
private typealias Rule = Pair<Page, Page>
private typealias Pages = List<Page>

fun main() {

    data class SafetyManualUpdate(
        val rules: List<Rule>,
        val updates: List<Pages>
    ) {
        val pagesAfter: Map<Page, List<Page>> by lazy { rules.groupBy({ it.first }) { it.second } }
        val pagesBefore: Map<Page, List<Page>> by lazy { rules.groupBy({ it.second }) { it.first } }
    }

    fun SafetyManualUpdate.isPageInOrder(pages: List<Page>, pageIndex: Int): Boolean {
        val beforeRules = (pagesBefore[pages[pageIndex]] ?: emptyList()).filter { p -> p in pages }
        val afterRules = (pagesAfter[pages[pageIndex]] ?: emptyList()).filter { p -> p in pages }

        val before = pages.subList(0, pageIndex)
        val after = pages.subList(pageIndex + 1, pages.size)

        return (beforeRules.isEmpty() || before.containsAll(beforeRules)) &&
                (afterRules.isEmpty() || after.containsAll(afterRules))
    }

    fun SafetyManualUpdate.isCorrectUpdate(pages: List<Page>): Boolean {
        return pages.withIndex().all { (index, _) ->
            isPageInOrder(pages, index)
        }
    }

    fun SafetyManualUpdate.fixUpdate(pages: List<Page>): List<Page> {
        return pages.sortedWith { p1, p2 ->
            when {
                /*(pagesBefore[p1] ?: emptyList()).contains(p2) -> 0
                (pagesAfter[p2] ?: emptyList()).contains(p1) -> 0*/
                (pagesBefore[p2] ?: emptyList()).contains(p1) -> -1
                (pagesAfter[p1] ?: emptyList()).contains(p2) -> 1
                else -> 0
            }
        }
    }

    fun List<String>.parseInput(): SafetyManualUpdate {
        val (rules, pagesToPrint) = fold(mutableListOf(mutableListOf<String>())) { acc, line ->
            if (line.isBlank()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(line)
            }
            acc
        }
        return SafetyManualUpdate(
            rules = rules.map { r -> r.split("|").let { (a, b) -> a.toInt() to b.toInt() } }.toList(),
            updates = pagesToPrint.map { p -> p.split(",").map { it.toInt() } }.toList()
        )
    }

    fun part1(input: SafetyManualUpdate): Int {
        return input.updates.filter { pages ->
            input.isCorrectUpdate(pages)
        }.sumOf { pages -> pages[(pages.size - 1) / 2] }
    }

    fun part2(input: SafetyManualUpdate): Int {
        return input.updates.filterNot { pages ->
            input.isCorrectUpdate(pages)
        }.map { pages -> input.fixUpdate(pages) }
            .sumOf { pages -> pages[(pages.size - 1) / 2] }
    }

    // Test if implementation meets criteria from the description, like:
    val testInput = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent().lines().parseInput()
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    // Or read a large test input from the `src/Day05_test.txt` file:
    //val testInput = readInput("Day05_test")
    //check(part1(testInput) == 1)

    // Read the input from the `src/Day05.txt` file.
    val input = readInput("Day05").parseInput()
    part1(input).println()
    part2(input).println()
}
