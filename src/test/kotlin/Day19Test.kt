package day19

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

enum class Material {
    ORE, CLAY, OBSIDIAN, GEODE
}

enum class Robot {
    ORE_ROBOT, CLAY_ROBOT, OBSIDIAN_ROBOT, GEODE_ROBOT
}

data class Recipe(val ingredients: Map<Material, Int>, val product: Robot)
data class Blueprint(val id: Int, val recipes: List<Recipe>)

fun <T> Map<T, Int>.toMaterialArray(indicies: List<T>): IntArray =
    indicies
        .map { this.getOrDefault(it, 0) }
        .toIntArray()

fun String.toBlueprint(): Blueprint {
    val parts = split(" ", ":").filterNot(String::isEmpty)

    return Blueprint(
        parts[1].toInt(),
        listOf(
            Recipe(mapOf(
                Material.ORE to parts[6].toInt()
            ), Robot.ORE_ROBOT),
            Recipe(mapOf(
                Material.ORE to parts[12].toInt()
            ), Robot.CLAY_ROBOT),
            Recipe(mapOf(
                Material.ORE to parts[18].toInt(),
                Material.CLAY to parts[21].toInt()
            ), Robot.OBSIDIAN_ROBOT),
            Recipe(mapOf(
                Material.ORE to parts[27].toInt(),
                Material.OBSIDIAN to parts[30].toInt()
            ), Robot.GEODE_ROBOT)
        )
    )
}

fun parseInput(input: String) =
    input.lines().filterNot(String::isEmpty).map(String::toBlueprint)


data class State(
    val ore: Int, val clay: Int, val obsidian: Int, val geodes: Int,
    val oreRobots: Int, val clayRobots: Int, val obsidianRobots: Int, val geodeRobots: Int,
    val time: Int)

fun findMaxGeodes(blueprint: Blueprint, totalTime: Int, cull: (Set<State>) -> Set<State>): Int {
    // Which robot are we saving up to construct?
    var toExplore = setOf(
        State(0, 0, 0, 0, 1, 0, 0, 0, 0)
    )

    var maxGeodesFound = 0
    var count = 0

    while (toExplore.isNotEmpty()) {
        val nextStates = mutableSetOf<State>()

        for (state in toExplore) {
            val (ore, clay, obsidian, geodes, oreRobots, clayRobots, obsidianRobots, geodeRobots, time) = state

            if (time == totalTime) {
                maxGeodesFound = max(maxGeodesFound, geodes)
                continue
            }

            val newOre = ore + oreRobots
            val newClay = clay + clayRobots
            val newObsidian = obsidian + obsidianRobots
            val newGeodes = geodes + geodeRobots

            val state =state.copy(
                ore = newOre,
                clay = newClay,
                obsidian = newObsidian,
                geodes = newGeodes,
                time = time + 1
            )
            nextStates.add(state)

//            println("Adding $state as a result of mining")

            for (recipe in blueprint.recipes) {
                val requiredOre = recipe.ingredients.getOrDefault(Material.ORE, 0)
                val requiredClay = recipe.ingredients.getOrDefault(Material.CLAY, 0)
                val requiredObsidian = recipe.ingredients.getOrDefault(Material.OBSIDIAN, 0)

                if (ore < requiredOre || clay < requiredClay || obsidian < requiredObsidian) continue

                val newOre2 = newOre - requiredOre
                val newClay2 = newClay - requiredClay
                val newObsidian2 = newObsidian - requiredObsidian

                val newOreRobots = oreRobots + if (recipe.product == Robot.ORE_ROBOT) { 1 } else { 0 }
                val newClayRobots = clayRobots + if (recipe.product == Robot.CLAY_ROBOT) { 1 } else { 0 }
                val newObsidianRobots = obsidianRobots + if (recipe.product == Robot.OBSIDIAN_ROBOT) { 1 } else { 0 }
                val newGeodeRobots = geodeRobots + if (recipe.product == Robot.GEODE_ROBOT) { 1 } else { 0 }

                val state = State(newOre2, newClay2, newObsidian2, newGeodes,
                    newOreRobots, newClayRobots, newObsidianRobots, newGeodeRobots, time + 1)

//                println("Adding $state as a result of $recipe")
                nextStates.add(state)
            }
        }

        println(nextStates.size)

        // Don't consider states where we have enough ingredients to make the most expensive robots.
        toExplore = cull(nextStates)

//        if (count < 5) {
//            println(count)
//            toExplore.forEach { println(it) }
//        } else {
//            return -1
//        }
        count += 1
    }

    return maxGeodesFound
}

fun throwAwayExcessMaterial(states: Set<State>, maxOre: Int, maxClay: Int, maxObsidian: Int): Set<State> {
    return states.map { state ->
        state.copy(
            ore = min(state.ore, maxOre),
            clay = min(state.clay, maxClay),
            obsidian = min(state.obsidian, maxObsidian)
        )
    }.toSet()
}

fun removeStrictlyWorseStates(states: Set<State>): Set<State> {
    return states.filterNot { state ->
        states.any { otherState ->
            state != otherState
                    && otherState.ore >= state.ore
                    && otherState.clay >= state.clay
                    && otherState.obsidian >= state.obsidian
                    && otherState.geodes >= state.geodes
                    && otherState.oreRobots >= state.oreRobots
                    && otherState.clayRobots >= state.clayRobots
                    && otherState.obsidianRobots >= state.obsidianRobots
                    && otherState.geodeRobots >= state.geodeRobots
        }
    }.toSet()
}

fun part1(input: String, totalTime: Int = 24): Long {
    val blueprints = parseInput(input)

    val cull = { states: Set<State> ->
        val newStates = throwAwayExcessMaterial(states, 30, 30, 30)
        val newStates2 = removeStrictlyWorseStates(newStates)
        println("Reduced states from ${states.size} to ${newStates.size} to ${newStates2.size}")
        newStates2
    }

    var result = 0L
    blueprints.forEachIndexed { index, blueprint ->
        println("Working on $index")
        result += blueprint.id * findMaxGeodes(blueprint, totalTime, cull).toLong()
    }

    return result
}

class Day19Test {
    @Test
    fun testParse() {
        parseInput(GIVEN_EXAMPLE)
    }

    @Test
    fun example1() {
        val blueprints = parseInput(GIVEN_EXAMPLE)

        val cull = { states: Set<State> ->
            val newStates = throwAwayExcessMaterial(states, 30, 30, 30)
            val newStates2 = removeStrictlyWorseStates(newStates)
            println("Reduced states from ${states.size} to ${newStates.size} to ${newStates2.size}")
            newStates2
        }

        assertEquals(9, findMaxGeodes(blueprints[0], 24, cull))
        assertEquals(12, findMaxGeodes(blueprints[1], 24, cull))

        assertEquals(33, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day19.txt").readText(Charsets.UTF_8)
        // 1490 is too low.
        // Runtime: 1m 30s
        assertEquals(1958, part1(input))
    }


    @Test
    fun example2() {
        val blueprints = parseInput(GIVEN_EXAMPLE)

        val cull = { states: Set<State> ->
            val newStates = throwAwayExcessMaterial(states, 30, 30, 30)
            val newStates2 = removeStrictlyWorseStates(newStates)
            println("Reduced states from ${states.size} to ${newStates.size} to ${newStates2.size}")
            newStates2
        }

        assertEquals(56, findMaxGeodes(blueprints[0], 32, cull))
        assertEquals(62, findMaxGeodes(blueprints[1], 32, cull))
    }

    @Test
    fun challenge2() {
        val input = File("input/day19.txt").readText(Charsets.UTF_8)
        val blueprints = parseInput(input)

        val cull = { states: Set<State> ->
            removeStrictlyWorseStates(throwAwayExcessMaterial(states, 30, 30, 30))
        }

        val time = 32
        // This takes 16 seconds
        val geodes0 = findMaxGeodes(blueprints[0], time, cull)
        val geodes1 = findMaxGeodes(blueprints[1], time, cull)
        val geodes2 = findMaxGeodes(blueprints[2], time, cull)

        assertEquals(4257, geodes0 * geodes1 * geodes2)
    }
}

const val GIVEN_EXAMPLE = """
Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian
"""