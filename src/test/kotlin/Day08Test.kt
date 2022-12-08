package day08

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

data class Coord(val x: Int, val y: Int)

data class Map(val contents: List<List<Int>>) {
    val width: Int
        get() = contents[0].size
    val height: Int
        get() = contents.size

    val coords: List<Coord>
        get() = (0 until height)
            .flatMap { y -> (0 until width).map{x -> Coord(x, y)} }
    fun at(c: Coord) = contents[c.y][c.x]

    fun getRow(y: Int) = contents[y]
    fun getColumn(x: Int) = contents.map { it[x] }
    fun getRowCoords(y: Int) = (0 until width).map { Coord(it, y) }
    fun getColumnCoords(x: Int) = (0 until height).map { Coord(x, it) }
}

fun parseMap(input: String): Map {
    return Map(
        input.lines()
            .filterNot(String::isEmpty)
            .map { line ->
                line.toList()
                    .map { it.toString().toInt() } }
    )
}

fun getVisibleTrees(heights: List<Int>, coords: List<Coord>): Set<Coord> {
    val visibleTrees: MutableSet<Coord> = mutableSetOf()
    var maxHeightSoFar = -1

    heights.zip(coords).forEach { (height, coord) ->
        if (height > maxHeightSoFar) {
            maxHeightSoFar = height
            visibleTrees.add(coord)
        }
    }

    return visibleTrees
}

fun getVisibleTrees(map: Map): Set<Coord> {
    val visibleTrees: MutableSet<Coord> = mutableSetOf()

    for (x in 0 until map.width) {
        val column = map.getColumn(x)
        val columnCoords = map.getColumnCoords(x)
        visibleTrees.addAll(getVisibleTrees(column, columnCoords))
        visibleTrees.addAll(getVisibleTrees(column.reversed(), columnCoords.reversed()))
    }

    for (y in 0 until map.height) {
        val row = map.getRow(y)
        val rowCoords = map.getRowCoords(y)
        visibleTrees.addAll(getVisibleTrees(row, rowCoords))
        visibleTrees.addAll(getVisibleTrees(row.reversed(), rowCoords.reversed()))
    }

    return visibleTrees
}

fun part1(input: String): Int = getVisibleTrees(parseMap(input)).size

fun getViewingDistances(trees: List<Int>): List<Int> {
    val viewingDistances = mutableListOf<Int>()
    val viewingDistanceForHeight = mutableMapOf<Int, Int>()

    // Initially the viewing distance for trees of any height is 0
    (0..9).forEach { viewingDistanceForHeight[it] = 0 }

    trees.forEachIndexed { index, height ->
        if (index == 0) {
            viewingDistances.add(0)
        } else {
            viewingDistances.add(viewingDistanceForHeight[height]!!)
        }

        (0..9).forEach { potentialHeight ->
            if (potentialHeight > height) {
                // We can see over the tree, to whatever we could have seen before, plus the new tree.
                viewingDistanceForHeight[potentialHeight] = viewingDistanceForHeight[potentialHeight]!! + 1
            } else {
                // We can only see the new tree.
                viewingDistanceForHeight[potentialHeight] = 1
            }
        }
    }

    return viewingDistances
}

enum class Direction {
    LEFT, UP, RIGHT, DOWN
}

// TODO: Don't recalculate everything so many times.
//fun getViewingDistanceMap(map: Map, direction: Direction): Map {
//    if (direction == Direction.LEFT) {
//        return Map(map.contents.map(::getViewingDistances))
//    } else if (direction == Direction.RIGHT) {
//        return Map(map.contents.reversed().map(::getViewingDistances).reversed())
//    } else if (direction == Direction.UP) {
//
//    } else {
//
//    }
//}

fun getViewingDistance(map: Map, coord: Coord): Int {
    println(coord)
    val leftDistance = getViewingDistances(map.getRow(coord.y))[coord.x]
    val rightDistance = getViewingDistances(map.getRow(coord.y).reversed()).reversed()[coord.x]
    val topDistance = getViewingDistances(map.getColumn(coord.x))[coord.y]
    val bottomDistance = getViewingDistances(map.getColumn(coord.x).reversed()).reversed()[coord.y]

    return leftDistance * rightDistance * topDistance * bottomDistance
}

fun part2(input: String): Int {
    val map = parseMap(input)
    return map.coords.maxOf {
        getViewingDistance(map, it)
    }
}

const val GIVEN_EXAMPLE: String = """
30373
25512
65332
33549
35390
"""

class Day08Test {
    @Test
    fun testMap() {
        val map = parseMap(GIVEN_EXAMPLE)
        assertEquals(5, map.width)
        assertEquals(5, map.height)

        assertEquals(listOf(2, 5, 5, 1, 2), map.getRow(1))
        assertEquals(listOf(7, 1, 3, 4, 9), map.getColumn(3))

        assertEquals(listOf(
            Coord(2, 0),
            Coord(2, 1),
            Coord(2, 2),
            Coord(2, 3),
            Coord(2, 4),
        ), map.getColumnCoords(2))
    }

    @Test
    fun example1() {
        assertEquals(21, getVisibleTrees(parseMap(GIVEN_EXAMPLE)).size)
    }

    @Test
    fun challenge1() {
        val input = File("input/day08.txt").readText(Charsets.UTF_8)
        assertEquals(1849, part1(input))
    }

    @Test
    fun getViewingDistance() {
        assertEquals(listOf(0, 1, 1, 1),
            getViewingDistances(listOf(0, 0, 0, 0)))
        assertEquals(listOf(0, 1, 1, 1),
            getViewingDistances(listOf(0, 1, 0, 0)))
        assertEquals(listOf(0, 1, 1, 2),
            getViewingDistances(listOf(0, 1, 0, 1)))
        assertEquals(listOf(0, 1, 1, 3),
            getViewingDistances(listOf(0, 1, 0, 2)))
    }

    @Test
    fun example2() {
        val map = parseMap(GIVEN_EXAMPLE)

        assertEquals(4, getViewingDistance(map, Coord(2, 1)))
        assertEquals(8, getViewingDistance(map, Coord(2, 3)))
        assertEquals(8, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        val input = File("input/day08.txt").readText(Charsets.UTF_8)
        assertEquals(0, part2(input))
    }
}