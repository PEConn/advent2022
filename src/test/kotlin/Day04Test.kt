import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

data class Range(val start: Int, val end: Int) {
    fun contains(point: Int): Boolean {
        return point in start..end
    }

    fun contains(other: Range): Boolean {
        return contains(other.start) && contains(other.end)
    }

    fun overlaps(other: Range): Boolean {
        return contains(other.start) || contains(other.end)
    }
}

private fun parseRange(string: String): Range {
    val parts = string.split('-')
    return Range(parts[0].toInt(), parts[1].toInt())
}

private fun parseLine(line: String): Pair<Range, Range> {
    val parts = line.split(',')
    return Pair(parseRange(parts[0]), parseRange(parts[1]))
}

private fun oneRangeWithin(line: String): Boolean {
    val ranges = parseLine(line)
    return ranges.first.contains(ranges.second) || ranges.second.contains(ranges.first)
}

private fun anyOverlap(line: String): Boolean {
    val ranges = parseLine(line)
    return ranges.first.overlaps(ranges.second) || ranges.second.overlaps(ranges.first)
}

private const val GIVEN_EXAMPLE: String = """
2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8
"""

private fun part1(input: String): Int {
    return input.lines()
        .filterNot(String::isEmpty)
        .count(::oneRangeWithin)
}

private fun part2(input: String): Int {
    return input.lines()
        .filterNot(String::isEmpty)
        .count(::anyOverlap)
}

class Day04Test {
    @Test
    fun example1() {
        assertEquals(2, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day04.txt").readText(Charsets.UTF_8)
        assertEquals(466, part1(input))
    }

    @Test
    fun example2() {
        assertEquals(4, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        val input = File("input/day04.txt").readText(Charsets.UTF_8)
        assertEquals(865, part2(input))
    }
}