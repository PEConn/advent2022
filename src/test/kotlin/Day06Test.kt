package day06

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

fun <E> List<E>.findMarker(markerLength: Int): Int {
    for (i in 0 until size - markerLength) {
        if (drop(i).take(markerLength).toSet().size == markerLength) {
            // The marker is at the end of the sequence, i points to the start.
            return i + markerLength
        }
    }

    throw java.lang.RuntimeException("Couldn't find marker")
}

class Day06Test {
    private fun testPart1(expected: Int, input: String) {
        assertEquals(expected, input.toList().findMarker(4))
    }

    private fun testPart2(expected: Int, input: String) {
        assertEquals(expected, input.toList().findMarker(14))
    }


    @Test fun example1() = testPart1(7, "mjqjpqmgbljsphdztnvjfqwrcgsmlb")
    @Test fun example2() = testPart1(5, "bvwbjplbgvbhsrlpgdmjqwftvncz")
    @Test fun example3() = testPart1(6, "nppdvjthqldpwncqszvftbrmjlhg")
    @Test fun example4() = testPart1(10, "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")
    @Test fun example5() = testPart1(11, "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")

    @Test
    fun challenge1() {
        testPart1(1275, File("input/day06.txt").readText(Charsets.UTF_8))
    }

    @Test fun example6() = testPart2(19, "mjqjpqmgbljsphdztnvjfqwrcgsmlb")
    @Test fun example7() = testPart2(23, "bvwbjplbgvbhsrlpgdmjqwftvncz")
    @Test fun example8() = testPart2(23, "nppdvjthqldpwncqszvftbrmjlhg")
    @Test fun example9() = testPart2(29, "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")
    @Test fun example10() = testPart2(26, "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")

    @Test
    fun challenge2() {
        testPart2(3605, File("input/day06.txt").readText(Charsets.UTF_8))
    }
}