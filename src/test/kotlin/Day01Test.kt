
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

const val GIVEN_EXAMPLE: String = """
1000
2000
3000

4000

5000
6000

7000
8000
9000

10000
"""

class Day01Test {

    @Test
    fun example1() {
        assertEquals(24000, day01part1(GIVEN_EXAMPLE))
    }

    @Test
    fun example2() {
        assertEquals(45000, day01part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day1.txt").readText(Charsets.UTF_8)
        assertEquals(71023, day01part1(input))
    }

    @Test
    fun challenge2() {
        val input = File("input/day1.txt").readText(Charsets.UTF_8)
        assertEquals(206289, day01part2(input))
    }
}