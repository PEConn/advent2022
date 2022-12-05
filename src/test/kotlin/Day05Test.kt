package day05

import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.test.assertEquals

private const val GIVEN_EXAMPLE: String = """
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
"""


data class State(val stacks: List<Stack<Char>>)
data class Move(val amount: Int, val from: Int, val to: Int)
data class Problem(val stacks: State, val moves: List<Move>)
fun parseProblem(input: String, numStacks: Int): Problem {
    var stacks = (0 until numStacks).map { Stack<Char>() }
    var moves = ArrayList<Move>()
    var parsingStacks = true

    input.lines().forEach {
        if (it.isEmpty()) {
            // Do nothing.
        } else if (it.startsWith(" 1 ")) {
            parsingStacks = false
        } else if (parsingStacks) {
            for (s in 0 until numStacks) {
                val c = it[1 + s*4]
                if (c != ' ') {
                    stacks[s].add(0, c)
                }
            }
        } else {
            val parts = it.split(' ')
            moves.add(Move(parts[1].toInt(), parts[3].toInt(), parts[5].toInt()))
        }
    }

    return Problem(State(stacks), moves)
}

fun Problem.run(part1: Boolean): State {
    val state = stacks // This modifies things, right?
    val moves = moves

    moves.forEach {
        if (part1) {
            for (i in 0 until it.amount) {
                state.stacks[it.to - 1].push(state.stacks[it.from - 1].pop())
            }
        } else {
            val insertionPoint = state.stacks[it.to - 1].size
            for (i in 0 until it.amount) {
                state.stacks[it.to - 1].add(insertionPoint, state.stacks[it.from - 1].pop())
            }
        }
    }

    return state
}
fun State.summarize(): String =
    stacks.map(Stack<Char>::peek).joinToString("")

fun <A> List<A>.toStack(): Stack<A> = Stack<A>().also { it.addAll(this) }

private fun part1(input: String, numStacks: Int): String {
    return parseProblem(input, numStacks).run(true).summarize()
}

private fun part2(input: String, numStacks: Int): String {
    return parseProblem(input, numStacks).run(false).summarize()
}

class Day05Test {
    @Test
    fun testParseProblem() {
        val expected = Problem(
            stacks = State(listOf(
                // These are reversed from the sample.
                listOf('Z', 'N').toStack(),
                listOf('M', 'C', 'D').toStack(),
                listOf('P').toStack(),
            )),
            moves = listOf(
                Move(1, 2, 1),
                Move(3, 1, 3),
                Move(2, 2, 1),
                Move(1, 1, 2),
            )
        )
        assertEquals(expected, parseProblem(GIVEN_EXAMPLE, 3))
    }

    @Test
    fun example1() {
        val expected = State(
            listOf(
                // These are reversed from the sample.
                listOf('C').toStack(),
                listOf('M').toStack(),
                listOf('P', 'D', 'N', 'Z').toStack(),
            )
        )

        val result = parseProblem(GIVEN_EXAMPLE, 3).run(true)

        assertEquals(expected, result)
        assertEquals("CMZ", result.summarize())
    }

    @Test
    fun challenge1() {
        val input = File("input/day05.txt").readText(Charsets.UTF_8)
        assertEquals("WHTLRMZRC", part1(input, 9))
    }

    @Test
    fun example2() {
        assertEquals("MCD", part2(GIVEN_EXAMPLE, 3))
    }

    @Test
    fun challenge2() {
        val input = File("input/day05.txt").readText(Charsets.UTF_8)
        assertEquals("GMPMLWNMG", part2(input, 9))
    }
}