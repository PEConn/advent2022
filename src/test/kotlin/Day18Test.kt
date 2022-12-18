package day18

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

enum class Axis {
    X, Y, Z
}

data class Point(val x: Int, val y: Int, val z: Int) {
    val neighbours
        get() = setOf(
        Point(x + 1, y, z),
        Point(x - 1, y, z),
        Point(x, y + 1, z),
        Point(x, y - 1, z),
        Point(x, y, z + 1),
        Point(x, y, z - 1),
    )

    fun getValue(axis: Axis) = when (axis) {
        Axis.X -> x
        Axis.Y -> y
        Axis.Z -> z
    }

    fun copy(axis: Axis, newValue: Int): Point = when (axis) {
        Axis.X -> Point(newValue, y, z)
        Axis.Y -> Point(x, newValue, z)
        Axis.Z -> Point(x, y, newValue)
    }
}

fun String.toPoint(): Point {
    val parts = split(",")
    return Point(
        parts[0].toInt(),
        parts[1].toInt(),
        parts[2].toInt()
    )
}

fun parseInput(input: String): Set<Point> =
    input.lines().filterNot(String::isEmpty).map(String::toPoint).toSet()

fun calculateSurfaceArea(points: Set<Point>): Int {
    return points.sumOf { point ->
        point.neighbours.count { neighbour ->
            !points.contains(neighbour)
        }
    }
}

fun calculateSurfaceArea(points: Set<Point>, outsidePoints: Set<Point>): Int {
    return points.sumOf { point ->
        point.neighbours.count { neighbour ->
            outsidePoints.contains(neighbour)
        }
    }
}

fun part1(input: String): Int = calculateSurfaceArea(parseInput(input))

fun getOutsidePoints(points: Set<Point>): Set<Point> {
    val minX = points.minOf { it.x } - 1
    val minY = points.minOf { it.y } - 1
    val minZ = points.minOf { it.z } - 1
    val maxX = points.maxOf { it.x } + 1
    val maxY = points.maxOf { it.y } + 1
    val maxZ = points.maxOf { it.z } + 1

    var toExplore = setOf(Point(minX, minY, minZ))
    val outsidePoints = mutableSetOf(Point(minX, minY, minZ))

    while (toExplore.isNotEmpty()) {
        toExplore = toExplore
            .flatMap { it.neighbours }
            .filterNot { it.x < minX || it.y < minY || it.z < minZ
                    || it.x > maxX || it.y > maxY || it.z > maxZ
                    || outsidePoints.contains(it) || points.contains(it) }
            .toSet()

        outsidePoints.addAll(toExplore)
    }

    return outsidePoints
}

fun part2(input: String): Int {
    val points = parseInput(input)
    val outsidePoints = getOutsidePoints(points)
    return calculateSurfaceArea(points, outsidePoints)
}

class Day18Test {
    @Test
    fun example1() {
        assertEquals(64, calculateSurfaceArea(parseInput(GIVEN_EXAMPLE)))
    }

    @Test
    fun challenge1() {
        val input = File("input/day18.txt").readText(Charsets.UTF_8)
        assertEquals(4548, part1(input))
    }

    @Test
    fun example2() {
        assertEquals(58, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        val input = File("input/day18.txt").readText(Charsets.UTF_8)
        // Not: 2579, too low.
        assertEquals(2588, part2(input))
    }
}

const val GIVEN_EXAMPLE: String = """
2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5
"""