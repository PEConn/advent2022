package day10

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class State {
    var cycle = 1
    var register = 1
    var signalStrength = 0L

    val screen = StringBuilder()

    fun tick() {
        if (cycle in setOf(20, 60, 100, 140, 180, 220)) {
            signalStrength += cycle * register
        }

        draw()
        cycle += 1
    }

    fun add(x: Int) {
        register += x
    }

    fun draw() {
        // On cycle 1, we draw the pixel 0.
        val x = (cycle - 1) % 40
        screen.append(if (register in (x-1)..(x+1)) { '#' } else { ' ' })

        if (x == 39) screen.append('\n')
    }
}

fun run(input: String): State {
    val state = State()

    input.lines().filterNot(String::isEmpty).forEach {
        val parts = it.split(" ")
        if (parts[0] == "noop") {
            state.tick()
        } else if (parts[0] == "addx") {
            state.tick()
            state.tick()
            state.add(parts[1].toInt())
        }
    }

    return state
}

class Day10Test {
    @Test
    fun basicExample1() {
        run("noop\naddx 3\naddx -5")
    }

    @Test
    fun example1() {
        assertEquals(13140, run(GIVEN_EXAMPLE).signalStrength)
    }

    @Test
    fun challenge1() {
        val input = File("input/day10.txt").readText(Charsets.UTF_8)
        assertEquals(14320, run(input).signalStrength)
    }

    @Test
    fun example2() {
        println(run(GIVEN_EXAMPLE).screen)
    }

    @Test
    fun challenge2() {
        val input = File("input/day10.txt").readText(Charsets.UTF_8)
        val expected = """
            ###   ##  ###  ###  #  #  ##  ###    ## 
            #  # #  # #  # #  # # #  #  # #  #    # 
            #  # #    #  # ###  ##   #  # #  #    # 
            ###  #    ###  #  # # #  #### ###     # 
            #    #  # #    #  # # #  #  # #    #  # 
            #     ##  #    ###  #  # #  # #     ##  
            
        """.trimIndent()
        assertEquals(expected, run(input).screen.toString())
    }
}

const val GIVEN_EXAMPLE: String = """
addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop
"""