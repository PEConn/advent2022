fun day01part1(input: String): Int {
    val maxElf = input.split("\n\n").maxOfOrNull { processElf(it) }
    return maxElf!!
}

fun day01part2(input: String): Int {
    val topElves = input
        .split("\n\n")
        .map { processElf(it) }
        .sortedDescending()
        .take(3)

    return topElves.sum()
}

fun processElf(calories: String): Int =
    calories.split("\n").filterNot { it.isEmpty() }.map{ it.toInt() }.sum()