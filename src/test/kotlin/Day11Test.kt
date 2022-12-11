package day10

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

typealias WorryLevel = Long
typealias InspectionOp = (x: Long) -> Long
typealias TargetingOp = (x: Long) -> Int

data class Monkey(val items: MutableList<WorryLevel>,
             val operation: InspectionOp,
             val findTarget: TargetingOp) {
    var inspections = 0
}

fun round(monkeys: List<Monkey>, discount: (WorryLevel) -> WorryLevel = { it / 3 }) {
    monkeys.forEach { monkey ->
        monkey.items.forEach { oldWorry ->

            monkey.inspections += 1
            val newWorryPreDiscount = monkey.operation(oldWorry)
            val newWorry = discount(newWorryPreDiscount)

            if (oldWorry > newWorryPreDiscount) {
                throw java.lang.RuntimeException("Overflow from $oldWorry to $newWorryPreDiscount.")
            }

            val target = monkey.findTarget(newWorry)

            monkeys[target].items.add(newWorry)
        }

        monkey.items.clear()
    }
}

fun part1(input: List<Monkey>): Int {
    (0 until 20).forEach { _ -> round(input)}

    val byInspections = input.sortedByDescending { it.inspections }

    return byInspections[0].inspections * byInspections[1].inspections
}

fun part2(input: List<Monkey>, divisor: Long): Long {
    (0 until 10000).forEach { _ -> round(input) { it % divisor } }

    val byInspections = input.sortedByDescending { it.inspections }
    return byInspections[0].inspections.toLong() * byInspections[1].inspections.toLong()
}

class Day11Test {
    @Test
    fun example1() {
        assertEquals(10605, part1(GIVEN_INPUT.duplicate()))
    }

    @Test
    fun challenge1() {
        assertEquals(64032, part1(TEST_INPUT.duplicate()))
    }

    @Test
    fun example2() {
        assertEquals(2713310158, part2(GIVEN_INPUT.duplicate(), 23 * 19 * 13 * 17))
    }

    @Test
    fun challenge2() {
        assertEquals(0, part2(TEST_INPUT.duplicate(), 2 * 3 * 5 * 7 * 11 * 13 * 17 * 19))
    }
}

fun List<Monkey>.duplicate(): List<Monkey> {
    return map { it.copy(items = it.items.toMutableList()) }
}

val GIVEN_INPUT = listOf(
    Monkey(
        items = mutableListOf(79, 98),
        operation = { it * 19 },
        findTarget = { if (it % 23 == 0L) { 2 } else { 3 } }
    ),
    Monkey(
        items = mutableListOf(54, 65, 75, 74),
        operation = { it + 6 },
        findTarget = { if (it % 19 == 0L) { 2 } else { 0 } }
    ),
    Monkey(
        items = mutableListOf(79, 60, 97),
        operation = { it * it },
        findTarget = { if (it % 13 == 0L) { 1 } else { 3 } }
    ),
    Monkey(
        items = mutableListOf(74),
        operation = { it + 3 },
        findTarget = { if (it % 17 == 0L) { 0 } else { 1 } }
    ),
)

val TEST_INPUT = listOf(
    Monkey(
        items = mutableListOf(83, 88, 96, 79, 86, 88, 70),
        operation = { it * 5 },
        findTarget = { if (it % 11 == 0L) { 2 } else { 3 } }
    ),
    Monkey(
        items = mutableListOf(59, 63, 98, 85, 68, 72),
        operation = { it * 11 },
        findTarget = { if (it % 5 == 0L) { 4 } else { 0 } }
    ),
    Monkey(
        items = mutableListOf(90, 79, 97, 52, 90, 94, 71, 70),
        operation = { it + 2 },
        findTarget = { if (it % 19 == 0L) { 5 } else { 6 } }
    ),
    Monkey(
        items = mutableListOf(97, 55, 62),
        operation = { it + 5 },
        findTarget = { if (it % 13 == 0L) { 2 } else { 6 } }
    ),
    Monkey(
        items = mutableListOf(74, 54, 94, 76),
        operation = { it * it },
        findTarget = { if (it % 7 == 0L) { 0 } else { 3 } }
    ),
    Monkey(
        items = mutableListOf(58),
        operation = { it + 4 },
        findTarget = { if (it % 17 == 0L) { 7 } else { 1 } }
    ),
    Monkey(
        items = mutableListOf(66, 63),
        operation = { it + 6 },
        findTarget = { if (it % 2 == 0L) { 7 } else { 5 } }
    ),
    Monkey(
        items = mutableListOf(56, 56, 90, 96, 68),
        operation = { it + 7 },
        findTarget = { if (it % 3 == 0L) { 4 } else { 1 } }
    ),
)