package day16

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.max
import kotlin.test.assertEquals

typealias ValveId = String

data class Valve(
    val name: ValveId,
    val flow: Int,
    val neighbours: List<String>) {
}

data class PossibleDestination(
    val valve: ValveId,
    val cost: Int
)

fun parseInput(input: String): List<Valve> {
    return input.lines()
        .filterNot(String::isEmpty)
        .map {
            val parts = it.split(" ", "=", ";", ",").filterNot(String::isEmpty)
            val name = parts[1]
            val flow = parts[5].toInt()
            val neighbours = parts.drop(10)
            Valve(name, flow, neighbours)
        }
}

data class Edge(
    val a: ValveId,
    val b: ValveId
)

data class EdgeWithCost(val edge: Edge, val cost: Int)

fun toEdge(a: ValveId, b: ValveId): Edge {
    return if (a <= b) {
        Edge(a, b)
    } else {
        Edge(b, a)
    }
}

fun getEdgesFrom(valves: Map<ValveId, Valve>, startingPoint: ValveId): List<EdgeWithCost> {
    val visited = mutableSetOf(startingPoint)
    var toVisit = valves[startingPoint]!!.neighbours
    var numSteps = 1

    val result = mutableListOf<EdgeWithCost>()

    while (toVisit.isNotEmpty()) {
        visited.addAll(toVisit)

        toVisit.forEach { other ->
            result.add(EdgeWithCost(toEdge(startingPoint, other), numSteps))
        }

        val toVisitNext = toVisit
            .flatMap { valves[it]!!.neighbours }
            .filterNot { visited.contains(it) }

        toVisit = toVisitNext
        numSteps += 1
    }

    return result
}

fun <T> forEachPermutation(values: MutableSet<T>,
                           stateSoFar: MutableList<T>,
                           op: (List<T>) -> Boolean) {
    if (op(stateSoFar)) return

    val valuesCopyForIterator = values.toSet()
    for (value in valuesCopyForIterator) {
        // Note: This assumes there are no duplicate values.
        values.remove(value)
        stateSoFar.add(value)
        forEachPermutation(values, stateSoFar, op)
        stateSoFar.removeLast()
        values.add(value)
    }
}

fun <T> forEachPermutation(values: Set<T>, start: T, op: (List<T>) -> Boolean) {
    val values = values.toMutableSet()
    values.remove(start)
    val stateSoFar = mutableListOf(start)
    forEachPermutation(values, stateSoFar, op)
}

fun List<ValveId>.getDuration(edges: Map<Edge, Int>): Int {
    return this.zip(this.drop(1)).sumOf { (a, b) -> edges[toEdge(a, b)]!! }
}

fun List<ValveId>.getTotalFlow(edges: Map<Edge, Int>, valveMap: Map<ValveId, Valve>) =
    getTotalFlow(edges, valveMap, 30)
fun List<ValveId>.getTotalFlow(edges: Map<Edge, Int>, valveMap: Map<ValveId, Valve>, duration: Int): Int {
    if (size < 2) return 0

    var timeToNextStop = edges[toEdge(this[0], this[1])]!!
    var nextStop = 1

    var currentFlow = 0
    var totalFlow = 0

    for (i in 0 until duration) {
//        println("Minute $i, releasing $currentFlow pressure.")
        totalFlow += currentFlow

        if (timeToNextStop == 0) {
            val thisValve = valveMap[this[nextStop]]!!

//            println("Opening ${thisValve.name}")
            currentFlow += thisValve.flow

            if (nextStop + 1 == size) {
                timeToNextStop = 30
            } else {
                val nextValve = valveMap[this[nextStop + 1]]!!
                // +1 to take into account the time taken to turn the valve on.
                timeToNextStop = edges[toEdge(thisValve.name, nextValve.name)]!! + 1
                nextStop += 1
            }
        }

        timeToNextStop -= 1
    }

    return totalFlow
}

fun part1(input: String): Int {
    val valves = parseInput(input)
    val importantValves = valves.filter { it.flow != 0 || it.name == "AA" }.map { it.name }.toSet()
    val valveMap = valves.associateBy { it.name }
    val edges = importantValves.flatMap { getEdgesFrom(valveMap, it) }
    val importantEdges = edges.filter { importantValves.contains(it.edge.a) && importantValves.contains(it.edge.b) }
    val edgeMap = importantEdges.associate { Pair(it.edge, it.cost) }
    // Maybe: importantEdges

    println("Valves: ${valves.size}")
    println("Important Valves: $importantValves")
    println("Edges: ${edges.size}")
    println("Important Edges: ${importantEdges.size}")
    val averageDistance = importantEdges.map { it.cost.toFloat() }.sum() / importantEdges.size
    println("Average Distance: ${averageDistance}")

    var count = 0
    var maxFlow = 0
    forEachPermutation(importantValves, "AA") {
        count += 1

//        println(it)
        val duration = it.getDuration(edgeMap)
        val flow = it.getTotalFlow(edgeMap, valveMap)
        maxFlow = max(flow, maxFlow)

        // Is there not a bug here...
        duration > 30
    }
    println(count)
    // 74756384
    return maxFlow
}

fun part2(input: String, time: Int): Int {
    val valves = parseInput(input)
    val importantValves = valves.filter { it.flow != 0 || it.name == "AA" }.map { it.name }.toSet()
    val valveMap = valves.associateBy { it.name }
    val edges = importantValves.flatMap { getEdgesFrom(valveMap, it) }
    val importantEdges = edges.filter { importantValves.contains(it.edge.a) && importantValves.contains(it.edge.b) }
    val edgeMap = importantEdges.associate { Pair(it.edge, it.cost) }
    // Maybe: importantEdges

    println("Valves: ${valves.size}")
    println("Important Valves: $importantValves")
    println("Edges: ${edges.size}")
    println("Important Edges: ${importantEdges.size}")
    val averageDistance = importantEdges.map { it.cost.toFloat() }.sum() / importantEdges.size
    println("Average Distance: ${averageDistance}")

    var count = 0L
    var maxFlow = 0

    val numCharacters = importantValves.size.toFloat()
    val firstCharacters = mutableSetOf<ValveId>()
    val secondCharacters = mutableSetOf<ValveId>()

    forEachPermutation(importantValves, "AA") {
        count += 1

        val route1 = mutableListOf<ValveId>()
        val route2 = mutableListOf("AA")
        var whichRoute = true
        it.forEach { x ->
            if (whichRoute) {
                route1.add(x)
            } else {
                route2.add(x)
            }
            whichRoute = !whichRoute
        }

        val duration1 = route1.getDuration(edgeMap)
        val duration2 = route2.getDuration(edgeMap)

        if (it.size > 1 && firstCharacters.add(it[1])) {
            secondCharacters.clear()
        }

        if (it.size > 2 && secondCharacters.add(it[2])) {
            println("Visiting ${it[1]} ${it[2]} (${firstCharacters.size / numCharacters}, ${secondCharacters.size / numCharacters})")
        }

        if (duration1 <= time && duration2 <= time) {
            val flow1 = route1.getTotalFlow(edgeMap, valveMap, time)
            val flow2 = route2.getTotalFlow(edgeMap, valveMap, time)
            if (flow1 + flow2 > maxFlow) {
                println("$route1, $route2: ${flow1 + flow2}")
            }
            maxFlow = max(flow1 + flow2, maxFlow)

            false
        } else {
            true
        }
    }
    println(count)
    return maxFlow
}

class Day16Test {
    @Test
    fun getTotalFlow2() {
        val valves = parseInput(GIVEN_EXAMPLE)
        val importantValves = valves.filter { it.flow != 0 || it.name == "AA" }.map { it.name }.toSet()
        val valveMap = valves.associateBy { it.name }
        val edges = importantValves.flatMap { getEdgesFrom(valveMap, it) }
        val importantEdges = edges.filter { importantValves.contains(it.edge.a) && importantValves.contains(it.edge.b) }
        val edgeMap = importantEdges.associate { Pair(it.edge, it.cost) }

        val route1 = listOf("AA", "JJ", "BB", "CC")
        val route2 = listOf("AA", "DD", "HH", "EE")

        val flow1 = route1.getTotalFlow(edgeMap, valveMap, 26)
        val flow2 = route2.getTotalFlow(edgeMap, valveMap, 26)

        assertEquals(1707, flow1 + flow2)
    }

    @Test
    fun getTotalFlow() {
        val valves = parseInput(GIVEN_EXAMPLE)
        val importantValves = valves.filter { it.flow != 0 || it.name == "AA" }.map { it.name }.toSet()
        val valveMap = valves.associateBy { it.name }
        val edges = importantValves.flatMap { getEdgesFrom(valveMap, it) }
        val importantEdges = edges.filter { importantValves.contains(it.edge.a) && importantValves.contains(it.edge.b) }
        val edgeMap = importantEdges.associate { Pair(it.edge, it.cost) }

        println("Important Valves: $importantValves")

        assertEquals(1651, listOf("AA", "DD", "BB", "JJ", "HH", "EE", "CC").getTotalFlow(edgeMap, valveMap))
    }

    @Test
    fun example1() {
        assertEquals(1651, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day16.txt").readText(Charsets.UTF_8)
        assertEquals(1986, part1(input))
    }

    @Test
    fun example2() {
        assertEquals(1707, part2(GIVEN_EXAMPLE, 26))
    }

    @Test
    fun challenge2() {
        val input = File("input/day16.txt").readText(Charsets.UTF_8)
        assertEquals(2464, part2(input, 26))
    }
}

const val GIVEN_EXAMPLE: String = """
Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II
"""