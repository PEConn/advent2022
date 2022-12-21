package day21

import getInput
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

typealias Value = Long

enum class Operation(val rep: String) {
    PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"), EQUALS("==")
}

fun String.toOperation(): Operation {
    return when (this) {
        "+" -> Operation.PLUS
        "-" -> Operation.MINUS
        "*" -> Operation.MULTIPLY
        "/" -> Operation.DIVIDE
        "==" -> Operation.EQUALS
        else -> throw IllegalArgumentException("Unexpected operation $this")
    }
}

fun Operation.apply(leftOperand: Value, rightOperand: Value): Value {
    return when (this) {
        Operation.PLUS -> leftOperand + rightOperand
        Operation.MINUS -> leftOperand - rightOperand
        Operation.MULTIPLY -> leftOperand * rightOperand
        Operation.DIVIDE -> leftOperand / rightOperand
        Operation.EQUALS -> throw IllegalStateException("Not meant to apply equals.")
    }
}

fun Operation.findLeftOperand(rightOperand: Value, result: Value): Value {
    return when (this) {
        Operation.PLUS -> result - rightOperand
        Operation.MINUS -> result + rightOperand
        Operation.MULTIPLY -> result / rightOperand
        Operation.DIVIDE -> result * rightOperand
        Operation.EQUALS -> rightOperand
    }
}

fun Operation.findRightOperand(leftOperand: Value, result: Value): Value {
    return when (this) {
        Operation.PLUS -> result - leftOperand
        Operation.MINUS -> leftOperand - result
        Operation.MULTIPLY -> result / leftOperand
        Operation.DIVIDE -> leftOperand / result
        Operation.EQUALS -> leftOperand
    }
}

interface Monkey {
    val id: String
}

data class ResolvedMonkey(override val id: String, val value: Value) : Monkey
data class UnresolvedMonkey(
    override val id: String,
    val leftOperand: String,
    val rightOperand: String,
    val operation: Operation) : Monkey

fun String.toMonkey(): Monkey {
    val parts = this.split(" ", ":").filterNot(String::isEmpty)
    return if (parts.size == 2) {
        ResolvedMonkey(parts[0], parts[1].toLong())
    } else if (parts.size == 4) {
        UnresolvedMonkey(
            parts[0],
            parts[1],
            parts[3],
            parts[2].toOperation()
        )
    } else {
        throw IllegalArgumentException("Could not parse Monkey $this")
    }
}

fun parseInput(input: String): Set<Monkey> =
    input.lines().filterNot(String::isEmpty).map(String::toMonkey).toSet()

fun part1(input: String): Value {
    val monkeys = parseInput(input)

    val resolvedMonkeys = monkeys
        .mapNotNull { if (it is ResolvedMonkey) { it } else { null } }
        .associateBy { it.id }
        .toMutableMap()
    val unresolvedMonkeys = monkeys
        .mapNotNull { if (it is UnresolvedMonkey) { it } else { null } }
        .associateBy { it.id }
        .toMutableMap()

    var count = 0

    while (unresolvedMonkeys.contains("root")) {
        // Copy so we can modify unresolvedMonkeys in the for-loop.
        val unresolvedMonkeyKeys = unresolvedMonkeys.keys.toSet()
        for (id in unresolvedMonkeyKeys) {
            val monkey = unresolvedMonkeys[id]!!

            val leftOperand = resolvedMonkeys[monkey.leftOperand]?.value
            val rightOperand = resolvedMonkeys[monkey.rightOperand]?.value

            if (leftOperand == null || rightOperand == null) continue

            unresolvedMonkeys.remove(id)
            resolvedMonkeys[id] = ResolvedMonkey(id,
                monkey.operation.apply(leftOperand, rightOperand))
        }
        count += 1
    }

    println("Took $count loops.")

    return resolvedMonkeys["root"]!!.value
}

fun part2(input: String): Value {
    val monkeys = parseInput(input)

    val resolvedMonkeys = monkeys
        .mapNotNull { if (it is ResolvedMonkey) { it } else { null } }
        .filterNot { it.id == "humn" }
        .associateBy { it.id }
        .toMutableMap()
    val unresolvedMonkeys = monkeys
        .mapNotNull { if (it is UnresolvedMonkey) { it } else { null } }
        .associateBy { it.id }
        .toMutableMap()

    var count = 0

    do {
        var changed = false

        val unresolvedMonkeyKeys = unresolvedMonkeys.keys.toSet()
        for (id in unresolvedMonkeyKeys) {
            val monkey = unresolvedMonkeys[id]!!

            val leftOperand = resolvedMonkeys[monkey.leftOperand]?.value
            val rightOperand = resolvedMonkeys[monkey.rightOperand]?.value

            if (leftOperand == null || rightOperand == null) continue

            changed = true
            unresolvedMonkeys.remove(id)
            resolvedMonkeys[id] = ResolvedMonkey(id,
                monkey.operation.apply(leftOperand, rightOperand))
        }

        count += 1
    } while (changed)

    println("Took $count loops.")
    println("${unresolvedMonkeys.size} monkeys unresolved.")

    // Update the value for root
    unresolvedMonkeys["root"] = unresolvedMonkeys["root"]!!.copy(operation = Operation.EQUALS)
    println(visit(resolvedMonkeys, unresolvedMonkeys, "root"))

    var next = "root"
    var requiredValue: Value = -1

    while (next != "humn") {
        val monkey = unresolvedMonkeys[next]!!

        if (resolvedMonkeys.contains(monkey.leftOperand)) {
            val value = resolvedMonkeys[monkey.leftOperand]!!.value

            requiredValue = monkey.operation.findRightOperand(value, requiredValue)
            next = monkey.rightOperand
        } else if (resolvedMonkeys.contains(monkey.rightOperand)) {
            val value = resolvedMonkeys[monkey.rightOperand]!!.value

            requiredValue = monkey.operation.findLeftOperand(value, requiredValue)
            next = monkey.leftOperand
        }
    }

    return requiredValue
}

fun visit(
    resolvedMonkeys: Map<String, ResolvedMonkey>,
    unresolvedMonkeys: Map<String, UnresolvedMonkey>,
    current: String): String {
    val resolvedMonkey = resolvedMonkeys[current]
    return if (current == "humn") {
        "x"
    } else if (resolvedMonkey != null) {
        resolvedMonkey.value.toString()
    } else {
        val unresolvedMonkey = unresolvedMonkeys[current]!!
        val left = visit(resolvedMonkeys, unresolvedMonkeys, unresolvedMonkey.leftOperand)
        val right = visit(resolvedMonkeys, unresolvedMonkeys, unresolvedMonkey.rightOperand)

        "($left ${unresolvedMonkey.operation.rep} $right)"
    }
}

class Day21Test {
    @Test
    fun example1() {
        assertEquals(152, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        assertEquals(63119856257960, part1(getInput(21)))
    }

    @Test
    fun example2() {
        assertEquals(301, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        assertEquals(3006709232464, part2(getInput(21)))
    }
}

const val GIVEN_EXAMPLE = """
root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32
"""