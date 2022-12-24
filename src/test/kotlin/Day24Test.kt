package day24

import getInput
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

data class Point(val x: Int, val y: Int)
enum class Direction(val x: Int, val y: Int) {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0),
}

fun Char.toDirection(): Direction? {
    return when (this) {
        '^' -> Direction.UP
        '>' -> Direction.RIGHT
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        else -> null
    }
}

data class MapInfo(val width: Int, val height: Int)

fun MapInfo.containsPoint(point: Point): Boolean {
    return point.x in 0 until width && point.y in 0 until height
}

data class Gust(val dir: Direction, val startingPoint: Point)

fun Int.wrap(base: Int): Int {
    // TODO: Make a note of this.
    if (this < 0) {
        return (base - (-this) % base) % base
    }

    return this % base
}

fun Gust.getLocationAtTime(map: MapInfo, time: Int): Point {
    val x = (startingPoint.x + dir.x * time).wrap(map.width)
    val y = (startingPoint.y + dir.y * time).wrap(map.height)
    return Point(x, y)
}

fun parseInput(input: String): Pair<MapInfo, List<Gust>> {
    val gusts = mutableListOf<Gust>()

    input.lines().filterNot(String::isEmpty).forEachIndexed { y, line ->
        // Ignore the first and last line.
        if (line[2] != '#') {
            line.toList().forEachIndexed { x, c ->
                val d = c.toDirection()

                // x-1 and y-1 because we're ignoring the walls.
                if (d != null) gusts.add(Gust(d, Point(x - 1, y - 1)))
            }
        }
    }

    // This assumes there is a Gust along each wall.
    val maxX = gusts.maxOf { it.startingPoint.x } + 1
    val maxY = gusts.maxOf { it.startingPoint.y } + 1

    return Pair(MapInfo(maxX, maxY), gusts)
}

fun findRoute(map: MapInfo, gusts: List<Gust>): Int {
    return findRoute(map, gusts, Point(0, -1), Point(map.width - 1, map.height - 1), 0)
}

fun findRoute(map: MapInfo, gusts: List<Gust>, source: Point, destination: Point, startTime: Int): Int {
    var states = setOf(source)
    var time = startTime

    while (true) {
        time += 1

        val blizzardPositions = gusts.map { it.getLocationAtTime(map, time) }.toSet()

        val nextStates = mutableSetOf<Point>()

        for (state in states) {
            var potentialLocations = mutableListOf(state)
            for (dir in Direction.values()) {
                val nextLoc = Point(state.x + dir.x, state.y + dir.y)

                if (map.containsPoint(nextLoc)) potentialLocations.add(nextLoc)
            }

            nextStates.addAll(potentialLocations.filterNot { blizzardPositions.contains(it) })
        }

        states = nextStates

        if (states.contains(destination)) {
            break
        }
    }

    // +1 to account for the final step.
    return time + 1
}

class Day24Test {
    @Test
    fun testAssumptions() {
        // We assume that there are no gusts that will be in the start or end point - in other words, there are no gusts
        // along the leftmost or rightmost rows that are heading up or down.

        val (mapInfo, gusts) = parseInput(getInput(24))
        assertEquals(0,
            gusts.filter { it.startingPoint.x == 0 }
                .filter { it.dir == Direction.UP || it.dir == Direction.DOWN }
                .size
        )
        assertEquals(0,
            gusts.filter { it.startingPoint.x == mapInfo.width }
                .filter { it.dir == Direction.UP || it.dir == Direction.DOWN }
                .size
        )
    }

    @Test
    fun testGustMovement() {
        val mapInfo = MapInfo(4, 4)
        val gust = Gust(Direction.RIGHT, Point(0, 0))

        assertEquals(Point(0, 0), gust.getLocationAtTime(mapInfo, 0))
        assertEquals(Point(1, 0), gust.getLocationAtTime(mapInfo, 1))
        assertEquals(Point(0, 0), gust.getLocationAtTime(mapInfo, 4))


        val mapInfo2 = MapInfo(6, 4)
        val gust2 = Gust(Direction.LEFT, Point(1, 1))

        assertEquals(Point(1, 1), gust2.getLocationAtTime(mapInfo2, 6))
    }

    @Test
    fun example1() {
        val (mapInfo, gusts) = parseInput(EXAMPLE)
        assertEquals(18, findRoute(mapInfo, gusts))
    }

    @Test
    fun challenge1() {
        val (mapInfo, gusts) = parseInput(getInput(24))
        println(mapInfo)
        assertEquals(271, findRoute(mapInfo, gusts))
    }

    @Test
    fun example2() {
        val (map, gusts) = parseInput(EXAMPLE)

        val trip1Start = Point(0, -1)
        val trip1End = Point(map.width - 1, map.height - 1)

        val trip2Start = Point(map.width - 1, map.height)
        val trip2End = Point(0, 0)

        val trip1 = findRoute(map, gusts, trip1Start, trip1End, 0)
        val trip2 = findRoute(map, gusts, trip2Start, trip2End, trip1)
        val trip3 = findRoute(map, gusts, trip1Start, trip1End, trip2)

        assertEquals(18, trip1)
        assertEquals(18 + 23, trip2)
        assertEquals(18 + 23 + 13, trip3)
    }

    @Test
    fun challenge2() {
        val (map, gusts) = parseInput(getInput(24))

        val trip1Start = Point(0, -1)
        val trip1End = Point(map.width - 1, map.height - 1)

        val trip2Start = Point(map.width - 1, map.height)
        val trip2End = Point(0, 0)

        val trip1 = findRoute(map, gusts, trip1Start, trip1End, 0)
        val trip2 = findRoute(map, gusts, trip2Start, trip2End, trip1)
        val trip3 = findRoute(map, gusts, trip1Start, trip1End, trip2)

        assertEquals(0, trip3)
    }
}

const val EXAMPLE: String = """
#.######
#>>.<^<#
#.<..<<#
#>v.><>#
#<^v^^>#
######.#
"""