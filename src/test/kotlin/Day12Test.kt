package day12

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

data class Coord(val x: Int, val y: Int) {
    fun neighbours(width: Int, height: Int): List<Coord> {
        return listOf(
//            Coord(x + 1, y + 1),
            Coord(x + 1, y),
//            Coord(x + 1, y - 1),
            Coord(x, y + 1),
            Coord(x, y - 1),
//            Coord(x - 1, y + 1),
            Coord(x - 1, y),
//            Coord(x - 1, y- 1),
        ).filter { it.x in (0 until width) && it.y in (0 until height) }
    }
}

data class Map(
    val contents: List<List<Int>>
) {
    val height get() = contents.size
    val width get() = contents[0].size
    val positions get() = (0 until width).flatMap { x -> (0 until height).map { y -> Coord(x, y) } }

    fun at(c: Coord): Int = contents[c.y][c.x]
}

fun Coord.neighbours(map: Map): List<Coord> = neighbours(map.width, map.height)
fun Coord.reachableNeighbours(map: Map): List<Coord> =
    neighbours(map).filter { map.at(it) <= map.at(this) + 1 }
fun Coord.reachableFromNeighbours(map: Map): List<Coord> =
    neighbours(map).filter { map.at(it) >= map.at(this) - 1 }

data class State(val map: Map, val start: Coord, val end: Coord)

fun parseInput(input: String): State {
    val lines = input.lines().filter(String::isNotEmpty)

    var start = Coord(-1, -1)
    var end = Coord(-1, -1)

    val map = lines.mapIndexed { y, line ->
        line.toList().mapIndexed { x, c ->
            when (c) {
                'S' -> {
                    start = Coord(x, y)
                    0
                }
                'E' -> {
                    end = Coord(x, y)
                    25
                }
                else -> {
                    c.code - 'a'.code
                }
            }
        }
    }

    return State(map = Map(map), start = start, end = end)
}

fun findDistance(state: State): Int {
    val map = state.map
    val visited = mutableSetOf(state.start)
    var neighbours = state.start.reachableNeighbours(map)
    var distance = 0

    while (!visited.contains(state.end)) {
        distance += 1

        visited.addAll(neighbours)
        neighbours = neighbours
            .flatMap { it.reachableNeighbours(map) }
            .toSet()
            .filterNot { visited.contains(it) }
    }

    return distance
}

fun findDistance2(state: State): Int {
    val map = state.map
    val visited = mutableSetOf(state.end)
    var neighbours = state.end.reachableFromNeighbours(map)
    var distance = 0

    while (!visited.any { map.at(it) == 0 }) {
        distance += 1

        visited.addAll(neighbours)
        neighbours = neighbours
            .flatMap { it.reachableFromNeighbours(map) }
            .toSet()
            .filterNot { visited.contains(it) }
    }

    return distance
}

class Day12Test {
    @Test
    fun testParse() {
        val state = parseInput(GIVEN_EXAMPLE)

        assertEquals(Coord(0, 0), state.start)
        assertEquals(Coord(5, 2), state.end)

        assertEquals(0, state.map.at(Coord(0, 0)))
        assertEquals(0, state.map.at(Coord(0, 1)))
        assertEquals(1, state.map.at(Coord(2, 0)))
        assertEquals(24, state.map.at(Coord(4, 1)))
    }

    @Test
    fun example1() {
        assertEquals(31, findDistance(parseInput(GIVEN_EXAMPLE)))
    }

    @Test
    fun challenge1() {
        val input = File("input/day12.txt").readText(Charsets.UTF_8)
        assertEquals(456, findDistance(parseInput(input)))
    }

    @Test
    fun example2() {
        assertEquals(29, findDistance2(parseInput(GIVEN_EXAMPLE)))
    }

    @Test
    fun challenge2() {
        val input = File("input/day12.txt").readText(Charsets.UTF_8)
        assertEquals(454, findDistance2(parseInput(input)))
    }
}

const val GIVEN_EXAMPLE: String = """
Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi
"""