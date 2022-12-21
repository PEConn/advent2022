package day20

import getInput
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

data class Coordinate(val id: Int, val value: Long)

fun parseInput(input: String): List<Coordinate> =
    input.lines()
        .filterNot(String::isEmpty)
        .map(String::toLong)
        .mapIndexed { i, value -> Coordinate(i, value) }

fun rearrange(coordinates: List<Coordinate>, times: Int = 1): List<Coordinate> {
    val coordinates = coordinates.toMutableList()
    val listSize = coordinates.size

    repeat(times) {
        for (i in 0 until listSize) {
            val index = coordinates.indexOfFirst { it.id == i }

            val coordinate = coordinates[index]
            val value = coordinates[index].value

            var destination = (index + value).wrap(listSize - 1).toInt()
            if (value < 0 && destination == 0)
                destination = listSize - 1


            coordinates.add(destination, coordinates.removeAt(index))
        }
    }

    return coordinates
}

fun Int.wrap(base: Int): Int {
    // TODO: Make a note of this.
    if (this < 0) {
        return (base - (-this) % base) % base
    }

    return this % base
}

fun Long.wrap(base: Int): Long {
    // TODO: Make a note of this.
    if (this < 0) {
        return (base - (-this) % base) % base
    }

    return this % base
}

fun part1(input: String): Long {
    val numbers = rearrange(parseInput(input)).map { it.value }
    val zero = numbers.indexOf(0)
    val size = numbers.size
    return numbers[(zero + 1000).wrap(size)] +
            numbers[(zero + 2000).wrap(size)] +
            numbers[(zero + 3000).wrap(size)]
}

fun part2(input: String): Long {
    val coords = parseInput(input).map { it.copy(value = it.value * 811589153) }

    val numbers = rearrange(coords, 10).map { it.value }
    val zero = numbers.indexOf(0)
    val size = numbers.size
    return numbers[(zero + 1000).wrap(size)] +
            numbers[(zero + 2000).wrap(size)] +
            numbers[(zero + 3000).wrap(size)]
}

class Day20Test {
    @Test
    fun wrap() {
        assertEquals(2, 2.wrap(10))
        assertEquals(2, 22.wrap(10))
        assertEquals(8, (-2).wrap(10))
        assertEquals(8, (-22).wrap(10))
        assertEquals(0, 0.wrap(10))
    }

    @Test
    fun example1() {
        val actual = rearrange(parseInput(GIVEN_EXAMPLE)).map { it.value.toInt() }
        assertEquals(
            listOf(1, 2, -3, 4, 0, 3, -2),
            actual
        )

        val zero = actual.indexOf(0)
        assertEquals(4, actual[(zero + 1000).wrap(actual.size)])
        assertEquals(-3, actual[(zero + 2000).wrap(actual.size)])
        assertEquals(2, actual[(zero + 3000).wrap(actual.size)])

        assertEquals(3, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day20.txt").readText(Charsets.UTF_8)
        assertEquals(8302, part1(input))
    }

    @Test
    fun example2() {
        assertEquals(1623178306, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        assertEquals(656575624777 ,part2(getInput(20)))
    }
}

const val GIVEN_EXAMPLE = """
1
2
-3
3
-2
0
4
"""