package day09

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.test.assertEquals

data class Coord(val x: Int, val y: Int) {
    fun move(direction: Direction): Coord {
        return when (direction) {
            Direction.UP -> Coord(x, y - 1)
            Direction.RIGHT -> Coord(x + 1, y)
            Direction.DOWN -> Coord(x, y + 1)
            Direction.LEFT -> Coord(x - 1, y)
        }
    }
}

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    companion object {
        fun from(s: String): Direction {
            return when (s) {
                "U" -> UP
                "R" -> RIGHT
                "D" -> DOWN
                "L" -> LEFT
                else -> throw java.lang.IllegalArgumentException("Unexpected direction: $s")
            }
        }
    }
}

fun roundAwayFromZero(x: Double): Int {
    return (if (x >= 0) { ceil(x) } else { floor(x) }).toInt()
}

fun Coord.moveTowards(target: Coord): Coord {
    if (target.x in (x-1)..(x+1) && target.y in (y-1)..(y+1)) {
        // We're touching, no need to move.
        return this
    }

    val xOffset = roundAwayFromZero((target.x - x) / 2.0)
    val yOffset = roundAwayFromZero((target.y - y) / 2.0)

    return Coord(
        x + xOffset,
        y + yOffset
    )
}

fun parseInput(input: String): List<Direction> {
    val moves = mutableListOf<Direction>()

    input.lines()
        .filterNot(String::isEmpty)
        .forEach {
            val parts = it.split(" ")
            val direction = Direction.from(parts[0])
            val numSteps = parts[1].toInt()

            // TODO: Does Kotlin have generators?
            (0 until numSteps).forEach{ _ -> moves.add(direction) }
        }

    return moves
}

fun simulate(moves: List<Direction>): Set<Coord> {
    var headPos = Coord(0, 0)
    var tailPos = Coord(0, 0)
    val allTailPositions = mutableSetOf(tailPos)

    moves.forEach {
        headPos = headPos.move(it)
        tailPos = tailPos.moveTowards(headPos)
        allTailPositions.add(tailPos)
    }

    return allTailPositions
}

fun simulate2(moves: List<Direction>): Set<Coord> {
    var head = Coord(0, 0)
    val segments = (1..9).map { _ -> Coord(0, 0) }.toMutableList()
    val allTailPositions = mutableSetOf(segments.last())

    moves.forEach {
        head = head.move(it)

        segments.indices.forEach { i ->
            if (i == 0) {
                segments[i] = segments[i].moveTowards(head)
            } else {
                segments[i] = segments[i].moveTowards(segments[i - 1])
            }
        }
        allTailPositions.add(segments.last())
    }

    println(segments)

    return allTailPositions
}

const val GIVEN_EXAMPLE: String = """
R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2
"""

const val GIVEN_EXAMPLE2: String = """
R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20
"""

class Day09Test {
    @Test
    fun testMoveTowards() {
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(1, 1)))

        // Still touching.
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(0, 0)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(0, 1)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(0, 2)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(1, 0)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(1, 2)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(2, 0)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(2, 1)))
        assertEquals(Coord(1, 1), Coord(1, 1).moveTowards(Coord(2, 2)))

        // Move along cardinal direction.
        assertEquals(Coord(2, 1), Coord(2, 2).moveTowards(Coord(2, 0)))
        assertEquals(Coord(1, 2), Coord(2, 2).moveTowards(Coord(0, 2)))
        assertEquals(Coord(3, 2), Coord(2, 2).moveTowards(Coord(4, 2)))
        assertEquals(Coord(2, 3), Coord(2, 2).moveTowards(Coord(2, 4)))

        // Move diagonally.
        assertEquals(Coord(3, 3), Coord(2, 2).moveTowards(Coord(3, 4)))
        assertEquals(Coord(3, 3), Coord(2, 2).moveTowards(Coord(4, 3)))

        assertEquals(Coord(1, 3), Coord(2, 2).moveTowards(Coord(1, 4)))
        assertEquals(Coord(1, 3), Coord(2, 2).moveTowards(Coord(0, 3)))

        assertEquals(Coord(1, 1), Coord(2, 2).moveTowards(Coord(0, 1)))
        assertEquals(Coord(1, 1), Coord(2, 2).moveTowards(Coord(1, 0)))

        assertEquals(Coord(3, 1), Coord(2, 2).moveTowards(Coord(4, 1)))
        assertEquals(Coord(3, 1), Coord(2, 2).moveTowards(Coord(3, 0)))
    }

    @Test
    fun example1() {
        assertEquals(13, simulate(parseInput(GIVEN_EXAMPLE)).size)
    }

    @Test
    fun challenge1() {
        val input = File("input/day09.txt").readText(Charsets.UTF_8)
        assertEquals(6464, simulate(parseInput(input)).size)
    }

    @Test
    fun example2() {
        assertEquals(36, simulate2(parseInput(GIVEN_EXAMPLE2)).size)
    }

    @Test
    fun challenge2() {
        val input = File("input/day09.txt").readText(Charsets.UTF_8)
        assertEquals(2604, simulate2(parseInput(input)).size)
    }
}