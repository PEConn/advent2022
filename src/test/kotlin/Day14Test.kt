package day14

import day12.findDistance
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class Coord(val x: Int, val y: Int)

fun String.toCoord(): Coord {
    val parts = split(",")
    return Coord(parts[0].toInt(), parts[1].toInt())
}

fun parseInput(input: String): Set<Coord> {
    val rocks = mutableSetOf<Coord>()
    input.lines().filterNot(String::isEmpty)
        .forEach {  line ->
            val coords = line
                .split(" -> ")
                .map(String::toCoord)

            coords.zip(coords.drop(1)) { a, b ->
                var c = a
                val xStep = if (b.x != a.x) { (b.x - a.x) / abs(b.x - a.x) } else { 0 }
                val yStep = if (b.y != a.y) { (b.y - a.y) / abs(b.y - a.y) } else { 0 }

                while (c != b) {
                    rocks.add(c)
                    c = Coord(c.x + xStep, c.y + yStep)
                }
                rocks.add(c)
            }
        }
    return rocks
}

fun run(rocks: Set<Coord>, source: Coord): Int {
    val settledSand = mutableSetOf<Coord>()
    val abyssY = rocks.maxOf { it.y }

    while (true) {
        val next = fall(rocks, settledSand, abyssY, source)

        if (next.y == abyssY) {
            return settledSand.size
        } else {
            settledSand.add(next)
        }
    }
}

fun fall(rocks: Set<Coord>, settledSand: Set<Coord>, abyssY: Int, sand: Coord): Coord {
    var sand = sand

    while (sand.y < abyssY) {
        sand = fallOneStep(rocks, settledSand, sand) ?: return sand
    }

    return sand
}

fun fallOneStep(rocks: Set<Coord>, settledSand: Set<Coord>, sand: Coord): Coord? {
    return listOf(
        Coord(sand.x, sand.y + 1),
        Coord(sand.x - 1, sand.y + 1),
        Coord(sand.x + 1, sand.y + 1)
    ).filterNot { rocks.contains(it) || settledSand.contains(it) }
        .firstOrNull()
}

fun draw(rocks: Set<Coord>, settledSand: Set<Coord>) {
    (0..9).forEach { y ->
        (494..503).forEach { x ->
            val c = Coord(x, y)
            if (rocks.contains(c)) {
                print('#')
            } else if (settledSand.contains(c)) {
                print('o')
            } else {
                print('.')
            }
        }
        println()
    }
    println()
}

fun run2(rocks: Set<Coord>, source: Coord): Int {
    val settledSand = mutableSetOf<Coord>()
    val floorY = rocks.maxOf { it.y } + 1

    while (true) {
        val next = fall(rocks, settledSand, floorY, source)

        if (next == source) {
            return settledSand.size + 1
        } else {
            settledSand.add(next)
        }
    }
}

class Day14Test {
    @Test
    fun testParse() {
        val rocks = parseInput(GIVEN_EXAMPLE)
        assertTrue(rocks.contains(Coord(498,4)))
        assertTrue(rocks.contains(Coord(498,5)))
        assertTrue(rocks.contains(Coord(498,6)))
        assertTrue(rocks.contains(Coord(497,6)))
        assertTrue(rocks.contains(Coord(496,6)))
    }

    @Test
    fun testRun() {
        assertEquals(24, run(parseInput(GIVEN_EXAMPLE), Coord(500,0)))
    }

    @Test
    fun challenge1() {
        val input = File("input/day14.txt").readText(Charsets.UTF_8)
        assertEquals(578, run(parseInput(input), Coord(500,0)))
    }

    @Test
    fun testRun2() {
        assertEquals(93, run2(parseInput(GIVEN_EXAMPLE), Coord(500,0)))
    }

    @Test
    fun challenge2() {
        val input = File("input/day14.txt").readText(Charsets.UTF_8)
        assertEquals(0, run2(parseInput(input), Coord(500,0)))
    }
}

const val GIVEN_EXAMPLE: String = """
498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9
"""