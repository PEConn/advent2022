package day

import getInput
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

data class RelativePoint(val x: Int, val y: Int)

enum class Direction(
    val move: RelativePoint,
    val neighbour1: RelativePoint,
    val neighbour2: RelativePoint
) {
    NORTH(RelativePoint(0, -1), RelativePoint(1, -1), RelativePoint(-1, -1)),
    SOUTH(RelativePoint(0, 1), RelativePoint(1, 1), RelativePoint(-1, 1)),
    WEST(RelativePoint(-1, 0), RelativePoint(-1, -1), RelativePoint(-1, 1)),
    EAST(RelativePoint(1, 0), RelativePoint(1, -1), RelativePoint(1, 1)),
}

data class Point(val x: Int, val y: Int)

fun Point.add(a: RelativePoint) = Point(x + a.x, y + a.y)

data class Elf(
    val loc: Point,
) {
    fun getNextLocation(occupiedPoints: Set<Point>, possibleMoves: List<Direction>): Point {
        // If no elves are around us, do nothing.
        var closeElves = 0
        for (x in -1..1) {
            for (y in -1 .. 1) {
                if (x == 0 && y == 0) continue

                if (occupiedPoints.contains(loc.add(RelativePoint(x, y)))) closeElves += 1
            }
        }

        if (closeElves == 0) {
            return loc
        }

        for (move in possibleMoves) {
            if (occupiedPoints.contains(loc.add(move.move)) ||
                    occupiedPoints.contains(loc.add(move.neighbour1)) ||
                    occupiedPoints.contains(loc.add(move.neighbour2))) {
                continue
            }
            return loc.add(move.move)
        }

        return loc
    }
}

fun parseInput(input: String): List<Elf> {
    val elves = mutableListOf<Elf>()
    input.lines().filterNot(String::isEmpty).forEachIndexed{ y, row ->
        row.toList().forEachIndexed { x, c ->
            if (c == '#') elves.add(Elf(Point(x, y)))
        }
    }
    return elves
}

fun move(elves: List<Elf>, moveNo: Int): List<Elf> {
    val possibleMoves = mutableListOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
    Collections.rotate(possibleMoves, -moveNo)

    // 1. Figure out where everyone wants to move to.
    val occupiedPoints = elves.map { it.loc }.toSet()

    val elvesWithNextMoves = elves.map { Pair(it, it.getNextLocation(occupiedPoints, possibleMoves)) }

    val nextOccupiedPoints = mutableMapOf<Point, Int>()
    elvesWithNextMoves.forEach { (_, point) ->
        nextOccupiedPoints[point] = nextOccupiedPoints.getOrDefault(point, 0) + 1
    }

    // 2. Move them if there are no collisions.
    return elvesWithNextMoves.map { (elf, nextLoc) ->
        if (nextOccupiedPoints[nextLoc]!! == 1) {
            Elf(nextLoc)
        } else {
            elf
        }
    }
}

fun boundingRectangleArea(elves: List<Elf>): Int {
    val minX = elves.minOf { it.loc.x }
    val maxX = elves.maxOf { it.loc.x }
    val minY = elves.minOf { it.loc.y }
    val maxY = elves.maxOf { it.loc.y }

    return (1 + maxY - minY) * (1 + maxX - minX)
}

fun part1(input: String): Int {
    var elves = parseInput(input)

    for (i in 0..9) {
        elves = move(elves, i)

        println(boundingRectangleArea(elves) - elves.size)
    }

    return boundingRectangleArea(elves) - elves.size
}

fun part2(input: String): Int {
    var elves = parseInput(input)
    var count = 0

    while (true) {
        val prevElves = elves.toSet()

        elves = move(elves, count)
        count += 1

        val newElves = elves.toSet()

        if (prevElves == newElves) break
    }

    return count
}

class Day23Test {
    @Test
    fun testParseInput() {
        assertEquals(5, parseInput(SMALLER_EXAMPLE).size)
    }

    @Test
    fun example1() {
        assertEquals(110, part1(EXAMPLE))
    }

    @Test
    fun challenge1() {
        assertEquals(3780, part1(getInput(23)))
    }

    @Test
    fun example2() {
        assertEquals(20, part2(EXAMPLE))
    }

    @Test
    fun challenge2() {
        assertEquals(930, part2(getInput(23)))
    }
}

const val EXAMPLE: String = """
....#..
..###.#
#...#.#
.#...##
#.###..
##.#.##
.#..#..
"""

const val SMALLER_EXAMPLE = """
.....
..##.
..#..
.....
..##.
.....
"""