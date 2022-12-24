package day15

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import pmap
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.assertEquals

data class Coord(val x: Long, val y: Long)
data class Sensor(val loc: Coord, val beacon: Coord) {
    val distance = abs(loc.x - beacon.x) + abs(loc.y - beacon.y)
}

/** start is inclusive, end is exclusive **/
data class Range(val start: Long, val end: Long)

fun Sensor.getOverlapAtY(y: Long): Range? {
    val distanceToY = abs(loc.y - y)
    val leftOverDistanceAtY = distance - distanceToY

    if (leftOverDistanceAtY <= 0) return null;

    return Range(loc.x - leftOverDistanceAtY, loc.x + leftOverDistanceAtY + 1)
}

fun List<Range>.combineOverlaps(): List<Range> {
    val sorted = sortedBy { it.start }
    var result = mutableListOf<Range>()

    var current = sorted.first()
    sorted.drop(1).forEach {
        if (it.start <= current.end) {
            // if overlap, expand current to cover it.
            // TODO: Note!
            current = Range(current.start, max(it.end, current.end))
        } else {
            // if no overlap, add current to the list, set current to this element
            result.add(current)
            current = it
        }
    }
    result.add(current)

    return result
}

fun String.toSensor(): Sensor {
    // Format = "Sensor at x=2, y=18: closest beacon is at x=-2, y=15"
    val parts = split(" ", "=", ",", ":").filterNot(String::isEmpty)

    return Sensor(
        Coord(parts[3].toLong(), parts[5].toLong()),
        Coord(parts[11].toLong(), parts[13].toLong())
    )
}

fun part1(input: String, y: Long): Long {
    val sensors = input.lines()
        .filterNot(String::isEmpty)
        .map(String::toSensor)

    val excludedLocations = sensors
        .mapNotNull { it.getOverlapAtY(y) }
        .combineOverlaps()

    val beaconsInOverlap = sensors
        .map { it.beacon }
        .filter { it.y == y }
        .distinct()
        .count()

    return excludedLocations.sumOf { it.end - it.start } - beaconsInOverlap
}

fun part2(input: String, maxY: Long): Long {
    val sensors = input.lines()
        .filterNot(String::isEmpty)
        .map(String::toSensor)

    return part2(sensors, 0, maxY)
}

fun part2(sensors: List<Sensor>, minY: Long, maxY: Long): Long {
    // This approach breaks down if the beacon is on the edge of the area.
    (minY..maxY).forEach { y ->
        val excludedLocations = sensors
            .mapNotNull { it.getOverlapAtY(y) }
            .combineOverlaps()

        if (excludedLocations.size > 1) {
            val x = excludedLocations.first().end
            return x * 4000000 + y
        }
    }

    return -1
}

fun part2parallel(input: String, maxY: Long): Long {
    val sensors = input.lines()
        .filterNot(String::isEmpty)
        .map(String::toSensor)

    val jobs = 25
    // 50: 2.4s
    // 25: 2.4s
    // 16: 2.2s
    // 8: 2.9s
    // 4: 2.3s
    val results = (0 until jobs).map{ 0L }.toMutableList()
    val step = maxY / jobs

    runBlocking {
        for (i in 0 until jobs) {
            launch {
                results[i] = part2(sensors, i * step, (i + 1) * step)
            }
        }
    }

    return results.first { it != -1L }
}

class Day15Test {
    @Test
    fun testToSensor() {
        assertEquals(Sensor(Coord(2, 18), Coord(-2, 15)),
            "Sensor at x=2, y=18: closest beacon is at x=-2, y=15".toSensor())
    }

    @Test
    fun testGetOverlap() {
        val sensor = "Sensor at x=8, y=7: closest beacon is at x=2, y=10".toSensor()
        assertEquals(Range(2, 15), sensor.getOverlapAtY(10))
    }

    @Test
    fun testCombineOverlaps() {
        assertEquals(
            listOf(Range(2, 5), Range(6, 8)),
            listOf(Range(2, 5), Range(6, 8)).combineOverlaps()
        )

        assertEquals(
            listOf(Range(2, 8)),
            listOf(Range(2, 5), Range(4, 8)).combineOverlaps()
        )

        assertEquals(
            listOf(Range(2, 8)),
            listOf(Range(2, 5), Range(5, 8)).combineOverlaps()
        )

        assertEquals(
            listOf(Range(2, 8)),
            listOf(Range(2, 5), Range(5, 8)).combineOverlaps()
        )
    }

    @Test
    fun example1() {
        assertEquals(26, part1(GIVEN_EXAMPLE, 10))
    }

    @Test
    fun part1() {
        val input = File("input/day15.txt").readText(Charsets.UTF_8)
        assertEquals(4883971, part1(input, 2000000))
    }

    @Test
    fun example2() {
        assertEquals(56000011, part2(GIVEN_EXAMPLE, 20))
    }

    @Test
    fun challenge2() {
        val input = File("input/day15.txt").readText(Charsets.UTF_8)
        val maxY = 4000000L

        // Runtimes:
        //   maxY = 4000L,  66ms
        //   maxY = 40000L, 115ms
        //   maxY = 400000L, 288ms
        //   maxY = 4000000L, 2472ms
        assertEquals(12691026767556, part2(input, 4000000L))
    }

    @Test
    fun challenge2parallel() {
        val input = File("input/day15.txt").readText(Charsets.UTF_8)

        assertEquals(12691026767556, part2parallel(input, 4000000L))
    }
}

const val GIVEN_EXAMPLE: String = """
Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3
"""