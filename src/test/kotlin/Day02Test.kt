
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private const val GIVEN_EXAMPLE: String = """A Y
B X
C Z
"""

enum class Move(val score: Int) {
    ROCK(1), PAPER(2), SCISSORS(3)
}

enum class Outcome(val score: Int) {
    LOSE(0), DRAW(3), WIN(6)
}

// Note: mapOf
private val moveMatrix: Map<Move, Map<Move, Outcome>> = mapOf(
    Move.ROCK to mapOf(
        Move.ROCK to Outcome.DRAW,
        Move.PAPER to Outcome.LOSE,
        Move.SCISSORS to Outcome.WIN,
    ),
    Move.PAPER to mapOf(
        Move.ROCK to Outcome.WIN,
        Move.PAPER to Outcome.DRAW,
        Move.SCISSORS to Outcome.LOSE,
    ),
    Move.SCISSORS to mapOf(
        Move.ROCK to Outcome.LOSE,
        Move.PAPER to Outcome.WIN,
        Move.SCISSORS to Outcome.DRAW,
    ),
)

private val toMyMove: Map<Char, Move> = mapOf(
    'X' to Move.ROCK,
    'Y' to Move.PAPER,
    'Z' to Move.SCISSORS,
)

private val toTheirMove: Map<Char, Move> = mapOf(
    'A' to Move.ROCK,
    'B' to Move.PAPER,
    'C' to Move.SCISSORS,
)

private val toOutcome: Map<Char, Outcome> = mapOf(
    'X' to Outcome.LOSE,
    'Y' to Outcome.DRAW,
    'Z' to Outcome.WIN,
)

fun toScorePart1(game: String): Int {
    val theirMove = toTheirMove[game[0]]!!
    val myMove = toMyMove[game[2]]!!

    val outcome = moveMatrix[myMove]!![theirMove]!!

    return myMove.score + outcome.score
}

fun toScorePart2(game: String): Int {
    val theirMove = toTheirMove[game[0]]!!
    val desiredOutcome = toOutcome[game[2]]!!

    // TODO: Preprocess to not do all the extra work.
    var myMove: Move? = null
    for (potentialMove in listOf(Move.ROCK, Move.PAPER, Move.SCISSORS)) {
        if (desiredOutcome == moveMatrix[potentialMove]!![theirMove]) {
            myMove = potentialMove
            break;
        }
    }

    return myMove!!.score + desiredOutcome.score
}

fun day01part1(input: String): Int {
    return input.lines().filterNot { it.isEmpty() }.sumOf { toScorePart1(it) }
}

fun day01part2(input: String): Int {
    return input.lines().filterNot { it.isEmpty() }.sumOf { toScorePart2(it) }
}

class Day02Test {

    @Test
    fun example1() {
        assertEquals(15, day01part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day02.txt").readText(Charsets.UTF_8)
        assertEquals(10595, day01part1(input))
    }

    @Test
    fun example2() {
        assertEquals(12, day01part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        val input = File("input/day02.txt").readText(Charsets.UTF_8)
        assertEquals(9541, day01part2(input))
    }
}