package day13

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.min
import kotlin.test.assertEquals

sealed interface Packet

data class ListPacket(val list: List<Packet>): Packet
data class ValuePacket(val value: Int): Packet

interface ListParseResult

class ListParseFailure : ListParseResult
data class ListParseSuccess(val list: ListPacket, val newOffset: Int) : ListParseResult

fun Int.v(): ValuePacket = ValuePacket(this)
fun List<Packet>.l() = ListPacket(this)
fun List<Int>.toListPacket() = ListPacket(this.map(Int::v))

fun tokenize(input: String): List<String> {
    return input.replace("[", " [ ")
        .replace("]", " ] ")
        .split(" ", ",")
        .filterNot(String::isEmpty)
}

fun parseList(tokens: List<String>, offset: Int): ListParseResult {
    if (tokens[offset] != "[") {
        return ListParseFailure()
    }

    val list = mutableListOf<Packet>()
    var i = offset + 1

    while (i < tokens.size && tokens[i] != "]") {
        val r = parseList(tokens, i)
        when (r) {
            is ListParseFailure -> {
                // Couldn't parse it as a list, therefore it's a value.
                list.add(tokens[i].toInt().v())
                i += 1
            }
            is ListParseSuccess -> {
                list.add(r.list)
                i = r.newOffset
            }
        }
    }

    return ListParseSuccess(ListPacket(list.toList()), i + 1)
}

enum class OrderResult(val comparison: Int) {
    RIGHT_ORDER(-1),
    WRONG_ORDER(1),
    COMPARE_NEXT(0)
}

fun inOrder(left: Packet, right: Packet): OrderResult {
    return when (left) {
        is ValuePacket -> {
            when (right) {
                is ValuePacket -> valuesInOrder(left, right)
                is ListPacket -> listsInOrder(ListPacket(listOf(left)), right)
            }
        }
        is ListPacket -> {
            when (right) {
                is ValuePacket -> listsInOrder(left, ListPacket(listOf(right)))
                is ListPacket -> listsInOrder(left, right)
            }
        }
    }
}

fun valuesInOrder(left: ValuePacket, right: ValuePacket): OrderResult {
    return if (left.value < right.value) {
        OrderResult.RIGHT_ORDER
    } else if (left.value > right.value) {
        OrderResult.WRONG_ORDER
    } else {
        OrderResult.COMPARE_NEXT
    }
}

fun listsInOrder(left: ListPacket, right: ListPacket): OrderResult {
    for (i in 0 until min(left.list.size, right.list.size)) {
        val ordering = inOrder(left.list[i], right.list[i])

        if (ordering == OrderResult.COMPARE_NEXT) continue

        return ordering
    }

    return if (left.list.size < right.list.size) {
        OrderResult.RIGHT_ORDER
    } else if (left.list.size > right.list.size) {
        OrderResult.WRONG_ORDER
    } else {
        OrderResult.COMPARE_NEXT
    }
}

fun parseAndCompare(left: String, right: String): OrderResult {
    println("Comparing $left and $right")
    val leftList = (parseList(tokenize(left), 0) as ListParseSuccess).list
    val rightList = (parseList(tokenize(right), 0) as ListParseSuccess).list

    return inOrder(leftList, rightList)
}

fun part1(input: String): Int {
    return input.split("\n", "\r")
        .filterNot(String::isEmpty)
        .chunked(2)
        .mapIndexed { index, strings ->
            println("$index : $strings")
            if (parseAndCompare(strings[0], strings[1]) == OrderResult.RIGHT_ORDER) {
                index + 1
            } else { 0}
        }.sum()
}

fun part2(input: String): Int {
    val packets = input.split("\n", "\r")
        .filterNot(String::isEmpty)
        .map { (parseList(tokenize(it), 0) as ListParseSuccess).list }

    val dividers = listOf(
        ListPacket(listOf(ListPacket(listOf(2.v())))),
        ListPacket(listOf(ListPacket(listOf(6.v()))))
    )

    val sorted =
        listOf(packets, dividers).flatten().sortedWith { left, right -> inOrder(left, right).comparison }

    val firstDividerLocation = sorted.indexOf(dividers[0]) + 1
    val secondDividerLocation = sorted.indexOf(dividers[1]) + 1

    return firstDividerLocation * secondDividerLocation
}

class Day13Test {
    @Test
    fun testParse1() {
        val expected = listOf(1, 1, 3, 1, 1).toListPacket()
        val actual = (parseList(tokenize("[1,1,3,1,1]"), 0) as ListParseSuccess).list

        assertEquals(expected, actual)
    }

    @Test
    fun testParse2() {
        val expected = listOf(listOf(1).toListPacket(), listOf(2, 3, 4).toListPacket()).l()
        val actual = (parseList(tokenize("[[1],[2,3,4]]"), 0) as ListParseSuccess).list

        assertEquals(expected, actual)
    }

    @Test
    fun testParse3() {
        val expected = ListPacket(listOf(ListPacket(listOf(ListPacket(listOf())))))
        val actual = (parseList(tokenize("[[[]]]"), 0) as ListParseSuccess).list

        assertEquals(expected, actual)
    }

    @Test
    fun testInOrder() {
        assertEquals(OrderResult.RIGHT_ORDER, parseAndCompare("[1,1,3,1,1]", "[1,1,5,1,1]"))
        assertEquals(OrderResult.RIGHT_ORDER, parseAndCompare("[[1],[2,3,4]]", "[[1],4]"))
        assertEquals(OrderResult.WRONG_ORDER, parseAndCompare("[9]", "[[8,7,6]]"))
        assertEquals(OrderResult.RIGHT_ORDER, parseAndCompare("[[4,4],4,4]", "[[4,4],4,4,4]"))
        assertEquals(OrderResult.WRONG_ORDER, parseAndCompare("[7,7,7,7]", "[7,7,7]"))
        assertEquals(OrderResult.RIGHT_ORDER, parseAndCompare("[]", "[3]"))
        assertEquals(OrderResult.WRONG_ORDER, parseAndCompare("[[[]]]", "[[]]"))
        assertEquals(OrderResult.WRONG_ORDER, parseAndCompare("[1,[2,[3,[4,[5,6,7]]]],8,9]", "[1,[2,[3,[4,[5,6,0]]]],8,9]"))
    }

    @Test
    fun example1() {
        assertEquals(13, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        val input = File("input/day13.txt").readText(Charsets.UTF_8)
        assertEquals(4643, part1(input))
    }

    @Test
    fun example2() {
        assertEquals(140, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        val input = File("input/day13.txt").readText(Charsets.UTF_8)
        assertEquals(21614, part2(input))
    }
}

const val GIVEN_EXAMPLE: String = """
[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]
"""