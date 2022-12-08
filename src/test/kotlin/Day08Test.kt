package day08

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

data class Coord(val x: Int, val y: Int)

data class Map(private val map: List<List<Int>>) {
    val width: Int
        get() = map[0].size
    val height: Int
        get() = map.size

    fun at(c: Coord) = map[c.y][c.x]

    fun getRow(y: Int) = map[y]
    fun getColumn(x: Int) = map.map { it[x] }
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

        println(viewingDistanceForHeight)
    }

//    val viewingDistances: MutableList<Int> = mutableListOf()
//
//    var topHeightSoFar = trees[0]
//    var mostVisibleTreeIndex = 0
//    viewingDistances.add(0)
//
//    trees.drop(1).forEachIndexed { i, height ->
//        if (height >= topHeightSoFar) {
//            viewingDistances.add(i - mostVisibleTreeIndex)
//            topHeightSoFar = height
//            mostVisibleTreeIndex = i
//        } else {
//            viewingDistances.add(1)
//        }
//    }

    return viewingDistances
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
//        assertEquals(listOf(0, 1, 1, 1),
//            getViewingDistances(listOf(0, 0, 0, 0)))
//        assertEquals(listOf(0, 1, 1, 1),
//            getViewingDistances(listOf(0, 1, 0, 0)))
//        assertEquals(listOf(0, 1, 1, 2),
//            getViewingDistances(listOf(0, 1, 0, 1)))
        assertEquals(listOf(0, 1, 1, 2),
            getViewingDistances(listOf(0, 1, 0, 2)))
    }
}