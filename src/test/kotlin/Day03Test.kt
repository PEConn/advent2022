import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private const val GIVEN_EXAMPLE: String = """
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
"""

private fun findDuplicate(string: String): Char {
    val midPoint = string.length / 2
    val charsInFirstHalf = HashSet(string.toList().take(midPoint))

    return string.toList().drop(midPoint).first { charsInFirstHalf.contains(it) }
}

private fun scoreChar(c: Char): Int {
    return when (c) {
        in 'a'..'z' -> c.code - 'a'.code + 1
        in 'A'..'Z' -> c.code - 'A'.code + 27
        else -> throw RuntimeException("Unexpected character: $c")
    }
}

private fun part1(input: String): Int =
    input.lines()
        .filterNot { it.isEmpty() }
        .sumOf { scoreChar(findDuplicate(it)) }

private fun findCommonItem(a: String, b: String, c:String): Char {
    val aChars = HashSet(a.toList())
    val bChars = HashSet(b.toList())
    return c.toList().first { aChars.contains(it) && bChars.contains(it) }
}

private fun part2(input: String): Int {
    return input.lines()
        .filterNot { it.isEmpty() }
        .chunked(3)
        .map { findCommonItem(it[0], it[1], it[2]) }
        .sumOf(::scoreChar)
}

class Day03Test {
    @Test
    fun testFindDuplicate() {
        assertEquals('p', findDuplicate("vJrwpWtwJgWrhcsFMMfFFhFp"))
        assertEquals('L', findDuplicate("jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL"))
        assertEquals('P', findDuplicate("PmmdzqPrVvPwwTWBwg"))
        assertEquals('v', findDuplicate("wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn"))
        assertEquals('t', findDuplicate("ttgJtRGJQctTZtZT"))
        assertEquals('s', findDuplicate("CrZsJsPPZsGzwwsLwLmpwMDw"))
    }

    @Test
    fun testScoreChar() {
        assertEquals(16, scoreChar('p'))
        assertEquals(38, scoreChar('L'))
        assertEquals(42, scoreChar('P'))
        assertEquals(22, scoreChar('v'))
        assertEquals(20, scoreChar('t'))
        assertEquals(19, scoreChar('s'))
    }

    @Test
    fun example1() {
        assertEquals(157, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day03.txt").readText(Charsets.UTF_8)
        assertEquals(7831, part1(input))
    }

    @Test
    fun testFindCommonItem() {
        assertEquals('r', findCommonItem(
            "vJrwpWtwJgWrhcsFMMfFFhFp",
            "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL",
            "PmmdzqPrVvPwwTWBwg"
        ))
        assertEquals('Z', findCommonItem(
            "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn",
            "ttgJtRGJQctTZtZT",
            "CrZsJsPPZsGzwwsLwLmpwMDw"
        ))
    }

    @Test
    fun example2() {
        assertEquals(70, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        val input = File("input/day03.txt").readText(Charsets.UTF_8)
        assertEquals(0, part2(input))
    }
}