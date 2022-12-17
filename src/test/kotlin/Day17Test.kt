package day17

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

data class Point(val x: Int, val y: Long) {
    fun move(direction: Direction): Point {
        return when(direction) {
            Direction.LEFT -> Point(x - 1, y)
            Direction.RIGHT -> Point(x + 1, y)
            Direction.DOWN -> Point(x, y + 1)
        }
    }
}

data class RelativePoint(val x: Int, val y: Int) {
    fun at(point: Point) = Point(x + point.x, y + point.y)
}

enum class Direction {
    LEFT, RIGHT, DOWN
}

typealias RP = RelativePoint
typealias Shape = List<RelativePoint>
val SHAPES: List<Shape> = listOf(
    // ####
    listOf(RP(0, 0), RP(1, 0), RP(2, 0), RP(3, 0),),

    //.#.
    //###
    //.#.
    listOf(RP(0, 0), RP(1, 0), RP(2, 0), RP(1, -1), RP(1, 1)),

    //..#
    //..#
    //###
    listOf(RP(0, 0), RP(1, 0), RP(2, 0), RP(2, -1), RP(2, -2)),

    //#
    //#
    //#
    //#
    listOf(RP(0, 0), RP(0, 1), RP(0, 2), RP(0, 3)),

    //##
    //##
    listOf(RP(0, 0), RP(1, 0), RP(0, 1), RP(1, 1))
)

fun parseInput(input: String): List<Direction> {
    return input.toList().map {
        when (it) {
            '>' -> Direction.RIGHT
            '<' -> Direction.LEFT
            else -> throw IllegalArgumentException("Unexpected character: $it")
        }
    }
}

fun Shape.at(position: Point): List<Point> = this.map { it.at(position) }

fun canMove(points: List<Point>, occupiedPoints: Set<Point>, width: Int): Boolean {
    if (points.any { it.x < 0 || it.x >= width || it.y > 0 }) return false

    return !points.any { occupiedPoints.contains(it) }
}

fun part1(input: String, numRocks: Long): Long {
    val wind = parseInput(input)

    val knownStates = mutableMapOf<List<Int>, Long>()

    var shapeIndex = 0
    var windIndex = 0

    // Start out with the floor occupied.
    val occupiedPoints = (0 .. 6).map { Point(it, 0) }.toMutableSet()

    for (i in 0 until numRocks) {
        if (i % 1_000 == 0L) println(i)

        val shape = SHAPES[shapeIndex]
        shapeIndex = (shapeIndex + 1) % SHAPES.size

        val rockHeightOffset = shape.maxOf { it.y }
        val maxY = occupiedPoints.minOf { it.y }

        // start 5 above the tallest point to account for rocks that are multiple blocks high.
        var rockPosition = Point(2, maxY - (4 + rockHeightOffset))

        while (true) {
//            draw(shape.at(rockPosition), occupiedPoints)

            val windDirection = wind[windIndex]
            windIndex = (windIndex + 1) % wind.size

            // Attempt to move the rock from wind.
            if (canMove(shape.at(rockPosition.move(windDirection)), occupiedPoints, 7)) {
                rockPosition = rockPosition.move(windDirection)
            }

            // Attempt to move the rock from gravity.
            if (canMove(shape.at(rockPosition.move(Direction.DOWN)), occupiedPoints, 7)) {
                rockPosition = rockPosition.move(Direction.DOWN)
            } else {
                occupiedPoints.addAll(shape.at(rockPosition))
                break
            }
        }

        // Only defrag after every x rocks:
        if (i % 10 == 0L) {
            // Delete any points below the lowermost occupied spot.
            // I think there's a bug here - what about overhangs?
            val maxNeededY = (0 until 7)
                .map { x -> occupiedPoints.filter { p -> p.x == x }
                    .minOf { p -> p.y }}.maxOf { it }
            occupiedPoints.removeIf { p -> (p.y > maxNeededY + 10) }
        }

        val floorPattern = (0 until 7)
            .map { x -> occupiedPoints.filter { p -> p.x == x }
                .minOf { p -> p.y }}
        val state = floorPattern.map { (it - floorPattern[0]).toInt() } + windIndex

        if (knownStates.contains(state)) {
            val prevStep = knownStates[state]
            throw Throwable("State at rock $i is a repeat of state at rock $prevStep")
        } else {
            knownStates[state] = i
        }
    }

    return -occupiedPoints.minOf { it.y }
}

class Day17Test {
    @Test
    fun example1() {
         assertEquals(3068, part1(GIVEN_EXAMPLE, 2022))
    }

    @Test
    fun challenge1() {
        val input = File("input/day17.txt").readText(Charsets.UTF_8)
        assertEquals(3065, part1(input, 2022))
    }

    @Test
    fun example2() {
        val numRocks = 1_000_000_000_000

        // numRocks = prefix + suffix + x * loop
        // [ prefix, loop, loop, loop, loop, ..., suffix ]

        val prefix = 27
        val firstRepeat = 62
        val loop = firstRepeat - prefix
        val numLoops = (numRocks - prefix) / loop

        val suffix = numRocks - prefix - loop*numLoops

        val state3 = prefix + suffix

        val heightAtSuffix = part1(GIVEN_EXAMPLE, prefix.toLong())
        val heightAtFirstRepeat = part1(GIVEN_EXAMPLE, firstRepeat.toLong())
        val heightPerLoop = heightAtFirstRepeat - heightAtSuffix

        val heightAtState3 = part1(GIVEN_EXAMPLE, state3.toLong())

        val totalHeight = heightAtState3 + numLoops * heightPerLoop

        assertEquals(1514285714288, totalHeight)
    }

    @Test
    fun challenge2() {
        val input = File("input/day17.txt").readText(Charsets.UTF_8)

        val numRocks = 1_000_000_000_000
//        part1(input, numRocks)

        // numRocks = prefix + suffix + x * loop
        // [ prefix, loop, loop, loop, loop, ..., suffix ]

//        val prefix = 1340
        val prefix = 1116
//        val firstRepeat = 2470
        val firstRepeat = 2851
        val loop = firstRepeat - prefix
        val numLoops = (numRocks - prefix) / loop

        val suffix = numRocks - prefix - loop*numLoops

        val state3 = prefix + suffix

        val heightAtSuffix = part1(input, prefix.toLong())
        val heightAtFirstRepeat = part1(input, firstRepeat.toLong())
        val heightPerLoop = heightAtFirstRepeat - heightAtSuffix

        val heightAtState3 = part1(input, state3)

        val totalHeight = heightAtState3 + numLoops * heightPerLoop

        assertEquals(1562536022966, totalHeight)
    }
}

const val GIVEN_EXAMPLE: String = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"